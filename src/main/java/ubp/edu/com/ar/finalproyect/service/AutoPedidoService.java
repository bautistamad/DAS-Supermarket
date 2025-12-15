package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.port.ProductoProveedorRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AutoPedidoService {

    private static final Logger logger = LoggerFactory.getLogger(AutoPedidoService.class);

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoProveedorRepository productoProveedorRepository;
    private final ProveedorIntegrationService proveedorIntegrationService;
    private final PedidoService pedidoService;

    public AutoPedidoService(ProductoRepository productoRepository,
                            ProveedorRepository proveedorRepository,
                            ProductoProveedorRepository productoProveedorRepository,
                            ProveedorIntegrationService proveedorIntegrationService,
                            PedidoService pedidoService) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoProveedorRepository = productoProveedorRepository;
        this.proveedorIntegrationService = proveedorIntegrationService;
        this.pedidoService = pedidoService;
    }

    @Transactional
    public Map<String, Object> generarPedidoAutomatico() {
        logger.info("Starting automatic order generation");

        // 1. Productos con stock bajo
        List<Producto> productosBajos = productoRepository.findProductosBajoStock();
        if (productosBajos.isEmpty()) {
            return buildResponse(true, "No hay productos con stock bajo", null, null, 0, 0.0f, null);
        }

        logger.info("Found {} products below minimum stock", productosBajos.size());

        // 2. Evaluar proveedores
        List<Proveedor> todosProveedores = proveedorRepository.findAll();
        // Filter only active providers
        List<Proveedor> proveedores = todosProveedores.stream()
            .filter(p -> p.getActivo() != null && p.getActivo())
            .toList();

        if (proveedores.isEmpty()) {
            return buildResponse(false, "No hay proveedores activos disponibles", null, null, 0, 0.0f, null);
        }

        logger.info("Evaluating {} active providers (out of {} total)", proveedores.size(), todosProveedores.size());
        Map<String, Object> mejorOpcion = null;

        for (Proveedor proveedor : proveedores) {
            logger.info("Checking active provider: {} (ID: {})", proveedor.getName(), proveedor.getId());

            // Preparar productos para este proveedor
            List<PedidoProducto> productosPedido = prepararProductosPedido(productosBajos, proveedor);
            if (productosPedido.isEmpty()) {
                logger.warn("Provider {} SKIPPED - does not have all required products", proveedor.getName());
                continue; // Proveedor no tiene todos los productos
            }

            logger.info("Provider {} has all {} required products", proveedor.getName(), productosPedido.size());

            // Estimar precio
            Pedido pedidoTemp = new Pedido();
            pedidoTemp.setProveedorId(proveedor.getId());
            pedidoTemp.setProductos(productosPedido);

            Map<String, Object> estimacion = proveedorIntegrationService.estimarPedidoWithProveedor(
                    proveedor.getId(), pedidoTemp);

            if (estimacion == null) {
                continue;
            }

            Float precioTotal = extractFloat(estimacion.get("precioEstimadoTotal"));
            Double rating = proveedor.getRatingPromedio() != null ? proveedor.getRatingPromedio() : 0.0;

            logger.info("Provider {} - Price: {}, Rating: {}", proveedor.getName(), precioTotal, rating);

            // Comparar con mejor opción
            if (mejorOpcion == null) {
                logger.info("First provider evaluated - setting as best option");
                mejorOpcion = new HashMap<>();
                mejorOpcion.put("proveedor", proveedor);
                mejorOpcion.put("precioTotal", precioTotal);
                mejorOpcion.put("rating", rating);
                mejorOpcion.put("productos", productosPedido);
                mejorOpcion.put("estimacion", estimacion); // Guardamos la estimación completa
            } else if (esMejorOpcion(precioTotal, rating, mejorOpcion)) {
                Float precioAnterior = (Float) mejorOpcion.get("precioTotal");
                Double ratingAnterior = (Double) mejorOpcion.get("rating");
                Proveedor proveedorAnterior = (Proveedor) mejorOpcion.get("proveedor");
                logger.info("Provider {} is better than {} (Old: ${}, rating {}) (New: ${}, rating {})",
                        proveedor.getName(), proveedorAnterior.getName(),
                        precioAnterior, ratingAnterior, precioTotal, rating);

                mejorOpcion = new HashMap<>();
                mejorOpcion.put("proveedor", proveedor);
                mejorOpcion.put("precioTotal", precioTotal);
                mejorOpcion.put("rating", rating);
                mejorOpcion.put("productos", productosPedido);
                mejorOpcion.put("estimacion", estimacion); // Guardamos la estimación completa
            } else {
                logger.info("Provider {} is NOT better than current best {} (Current: ${}, rating {}) (This: ${}, rating {})",
                        proveedor.getName(), ((Proveedor)mejorOpcion.get("proveedor")).getName(),
                        mejorOpcion.get("precioTotal"), mejorOpcion.get("rating"), precioTotal, rating);
            }
        }

        if (mejorOpcion == null) {
            return buildResponse(false, "Ningún proveedor puede suplir todos los productos", null, null, 0, 0.0f, null);
        }

        // 3. Crear pedido con mejor proveedor
        Proveedor ganador = (Proveedor) mejorOpcion.get("proveedor");
        @SuppressWarnings("unchecked")
        List<PedidoProducto> productos = (List<PedidoProducto>) mejorOpcion.get("productos");
        @SuppressWarnings("unchecked")
        Map<String, Object> estimacionGanadora = (Map<String, Object>) mejorOpcion.get("estimacion");

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setProveedorId(ganador.getId());
        nuevoPedido.setEstadoId(1); // Pendiente
        nuevoPedido.setProductos(productos);

        // Setear fechaEstimada desde la estimación del proveedor
        if (estimacionGanadora != null && estimacionGanadora.get("fechaEstimada") != null) {
            Object fechaEstimadaObj = estimacionGanadora.get("fechaEstimada");
            if (fechaEstimadaObj instanceof LocalDateTime) {
                nuevoPedido.setFechaEstimada((LocalDateTime) fechaEstimadaObj);
            } else if (fechaEstimadaObj instanceof String) {
                nuevoPedido.setFechaEstimada(LocalDateTime.parse((String) fechaEstimadaObj));
            }
        }

        Pedido pedidoCreado = pedidoService.createPedido(nuevoPedido);

        logger.info("Order created: ID={}, Provider={}", pedidoCreado.getId(), ganador.getName());

        return buildResponse(true, "Pedido automático creado exitosamente",
                pedidoCreado.getId(), ganador.getName(), productos.size(),
                (Float) mejorOpcion.get("precioTotal"), (Double) mejorOpcion.get("rating"));
    }

    private List<PedidoProducto> prepararProductosPedido(List<Producto> productosBajos, Proveedor proveedor) {
        List<PedidoProducto> productosPedido = new ArrayList<>();

        for (Producto producto : productosBajos) {
            var productoProveedor = productoProveedorRepository.findByProveedorAndProducto(
                    proveedor.getId(), producto.getCodigoBarra());

            if (productoProveedor == null || productoProveedor.getEstado() != 1) {
                return new ArrayList<>(); // Proveedor no tiene todos
            }

            Integer cantidad = producto.getMaxStock() - producto.getActualStock();
            if (cantidad > 0) {
                PedidoProducto pp = new PedidoProducto();
                pp.setCodigoBarra(producto.getCodigoBarra());
                pp.setCodigoBarraProveedor(productoProveedor.getCodigoBarraProveedor());
                pp.setCantidad(cantidad);
                productosPedido.add(pp);
            }
        }

        return productosPedido;
    }

    private boolean esMejorOpcion(Float precio, Double rating, Map<String, Object> mejorActual) {
        Float precioMejor = (Float) mejorActual.get("precioTotal");
        Double ratingMejor = (Double) mejorActual.get("rating");

        if (precio < precioMejor) {
            return true;
        }
        if (precio.equals(precioMejor) && rating > ratingMejor) {
            return true;
        }
        return false;
    }

    private Map<String, Object> buildResponse(boolean exito, String mensaje, Integer pedidoId,
                                              String proveedor, int productosCount, Float costo, Double rating) {
        Map<String, Object> response = new HashMap<>();
        response.put("exito", exito);
        response.put("mensaje", mensaje);
        response.put("pedidoId", pedidoId);
        response.put("proveedorSeleccionado", proveedor);
        response.put("productosOrdenados", productosCount);
        response.put("costoTotal", costo);
        response.put("ratingProveedor", rating);
        return response;
    }

    private Float extractFloat(Object value) {
        if (value == null) return 0.0f;
        if (value instanceof Float) return (Float) value;
        if (value instanceof Double) return ((Double) value).floatValue();
        if (value instanceof Integer) return ((Integer) value).floatValue();
        return Float.parseFloat(value.toString());
    }
}
