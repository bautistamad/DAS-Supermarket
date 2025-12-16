package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.ProductoProveedor;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.domain.subdomain.AsignacionProducto;
import ubp.edu.com.ar.finalproyect.domain.subdomain.EstadisticasGeneracion;
import ubp.edu.com.ar.finalproyect.domain.subdomain.OfertaProveedor;
import ubp.edu.com.ar.finalproyect.domain.subdomain.PedidoResumen;
import ubp.edu.com.ar.finalproyect.port.HistorialPrecioRepository;
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

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoProveedorRepository productoProveedorRepository;
    private final HistorialPrecioRepository historialPrecioRepository;
    private final ProveedorIntegrationService proveedorIntegrationService;
    private final PedidoService pedidoService;

    public AutoPedidoService(ProductoRepository productoRepository,
                            ProveedorRepository proveedorRepository,
                            ProductoProveedorRepository productoProveedorRepository,
                            HistorialPrecioRepository historialPrecioRepository,
                            ProveedorIntegrationService proveedorIntegrationService,
                            PedidoService pedidoService) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoProveedorRepository = productoProveedorRepository;
        this.historialPrecioRepository = historialPrecioRepository;
        this.proveedorIntegrationService = proveedorIntegrationService;
        this.pedidoService = pedidoService;
    }

    @Transactional
    public Map<String, Object> generarPedidoAutomatico() {
        List<Producto> productosBajos = productoRepository.findProductosBajoStock();
        if (productosBajos.isEmpty()) {
            return buildResponse(true, "No hay productos con stock bajo", null, null, 0, 0.0f, null);
        }

        List<Proveedor> proveedores = proveedorRepository.findAll().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .toList();

        if (proveedores.isEmpty()) {
            return buildResponse(false, "No hay proveedores activos disponibles", null, null, 0, 0.0f, null);
        }

        Map<String, Object> mejorOpcion = null;

        for (Proveedor proveedor : proveedores) {
            List<PedidoProducto> productosPedido = prepararProductosPedido(productosBajos, proveedor);
            if (productosPedido.isEmpty()) {
                continue;
            }

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

            if (mejorOpcion == null) {
                mejorOpcion = new HashMap<>();
                mejorOpcion.put("proveedor", proveedor);
                mejorOpcion.put("precioTotal", precioTotal);
                mejorOpcion.put("rating", rating);
                mejorOpcion.put("productos", productosPedido);
                mejorOpcion.put("estimacion", estimacion);
            } else if (esMejorOpcion(precioTotal, rating, mejorOpcion)) {
                mejorOpcion = new HashMap<>();
                mejorOpcion.put("proveedor", proveedor);
                mejorOpcion.put("precioTotal", precioTotal);
                mejorOpcion.put("rating", rating);
                mejorOpcion.put("productos", productosPedido);
                mejorOpcion.put("estimacion", estimacion);
            }
        }

        if (mejorOpcion == null) {
            return buildResponse(false, "Ningún proveedor puede suplir todos los productos", null, null, 0, 0.0f, null);
        }

        Proveedor ganador = (Proveedor) mejorOpcion.get("proveedor");
        @SuppressWarnings("unchecked")
        List<PedidoProducto> productos = (List<PedidoProducto>) mejorOpcion.get("productos");
        @SuppressWarnings("unchecked")
        Map<String, Object> estimacionGanadora = (Map<String, Object>) mejorOpcion.get("estimacion");

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setProveedorId(ganador.getId());
        nuevoPedido.setEstadoId(1);
        nuevoPedido.setProductos(productos);

        if (estimacionGanadora != null && estimacionGanadora.get("fechaEstimada") != null) {
            Object fechaEstimadaObj = estimacionGanadora.get("fechaEstimada");
            if (fechaEstimadaObj instanceof LocalDateTime) {
                nuevoPedido.setFechaEstimada((LocalDateTime) fechaEstimadaObj);
            } else if (fechaEstimadaObj instanceof String) {
                nuevoPedido.setFechaEstimada(LocalDateTime.parse((String) fechaEstimadaObj));
            }
        }

        Pedido pedidoCreado = pedidoService.createPedido(nuevoPedido);

        return buildResponse(true, "Pedido automático creado exitosamente",
                pedidoCreado.getId(), ganador.getName(), productos.size(),
                (Float) mejorOpcion.get("precioTotal"), (Double) mejorOpcion.get("rating"));
    }

    @Transactional
    public Map<String, Object> generarPedidoAutomaticoOptimizado() {
        EstadisticasGeneracion estadisticas = new EstadisticasGeneracion();

        List<Producto> productosBajos = productoRepository.findProductosBajoStock();
        estadisticas.setTotalProductosProcesados(productosBajos.size());

        if (productosBajos.isEmpty()) {
            return buildSuccessResponse("No hay productos con stock bajo", List.of(), List.of(), estadisticas);
        }

        List<Proveedor> proveedoresActivos = proveedorRepository.findAll().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .toList();

        if (proveedoresActivos.isEmpty()) {
            return buildErrorResponse("No hay proveedores activos disponibles", estadisticas);
        }

        sincronizarPreciosProveedores(proveedoresActivos);

        Map<Integer, List<AsignacionProducto>> pedidosTemporales =
                asignarProductosPorMejorPrecio(productosBajos, proveedoresActivos, estadisticas);

        if (pedidosTemporales.isEmpty()) {
            return buildErrorResponse("Ningún proveedor puede suplir los productos necesarios", estadisticas);
        }

        consolidarPedidosUnitarios(pedidosTemporales, proveedoresActivos, estadisticas);

        List<PedidoResumen> ordenesGeneradas = crearPedidosFinales(pedidosTemporales, proveedoresActivos, estadisticas);

        return buildSuccessResponse("Pedidos generados exitosamente", ordenesGeneradas, List.of(), estadisticas);
    }

    private void sincronizarPreciosProveedores(List<Proveedor> proveedoresActivos) {
        for (Proveedor proveedor : proveedoresActivos) {
            try {
                proveedorIntegrationService.syncProductosFromProveedor(proveedor.getId());
            } catch (Exception e) {
                continue;
            }
        }
    }

    private Map<Integer, List<AsignacionProducto>> asignarProductosPorMejorPrecio(
            List<Producto> productosBajos,
            List<Proveedor> proveedoresActivos,
            EstadisticasGeneracion estadisticas) {

        Map<Integer, List<AsignacionProducto>> pedidosTemporales = new HashMap<>();

        for (Producto producto : productosBajos) {
            List<OfertaProveedor> ofertas = buscarOfertasProducto(producto, proveedoresActivos);

            if (ofertas.isEmpty()) {
                estadisticas.incrementarProductosNoAsignados();
                continue;
            }

            OfertaProveedor mejorOferta = seleccionarMejorOferta(ofertas);

            Integer cantidad = producto.getMaxStock() - producto.getActualStock();
            if (cantidad <= 0) {
                continue;
            }

            AsignacionProducto asignacion = new AsignacionProducto(
                    producto.getCodigoBarra(),
                    mejorOferta.getCodigoBarraProveedor(),
                    cantidad,
                    mejorOferta.getPrecio(),
                    producto.getNombre()
            );

            pedidosTemporales
                    .computeIfAbsent(mejorOferta.getProveedorId(), k -> new ArrayList<>())
                    .add(asignacion);

            estadisticas.incrementarProductosAsignados();
        }

        return pedidosTemporales;
    }

    private List<OfertaProveedor> buscarOfertasProducto(Producto producto, List<Proveedor> proveedoresActivos) {
        List<OfertaProveedor> ofertas = new ArrayList<>();

        for (Proveedor proveedor : proveedoresActivos) {
            ProductoProveedor productoProveedor =
                    productoProveedorRepository.findByProveedorAndProducto(proveedor.getId(), producto.getCodigoBarra());

            if (productoProveedor == null || productoProveedor.getEstado() != 1) {
                continue;
            }

            HistorialPrecio precioHistorial =
                    historialPrecioRepository.getCurrentPrice(producto.getCodigoBarra(), proveedor.getId());

            if (precioHistorial == null) {
                continue;
            }

            OfertaProveedor oferta = new OfertaProveedor(
                    proveedor.getId(),
                    proveedor.getName(),
                    productoProveedor.getCodigoBarraProveedor(),
                    precioHistorial.getPrecio(),
                    proveedor.getRatingPromedio()
            );

            ofertas.add(oferta);
        }

        return ofertas;
    }

    private OfertaProveedor seleccionarMejorOferta(List<OfertaProveedor> ofertas) {
        return ofertas.stream()
                .min((o1, o2) -> {
                    int precioComparison = Float.compare(o1.getPrecio(), o2.getPrecio());
                    if (precioComparison != 0) {
                        return precioComparison;
                    }
                    return Double.compare(o2.getRating(), o1.getRating());
                })
                .orElseThrow();
    }

    private void consolidarPedidosUnitarios(
            Map<Integer, List<AsignacionProducto>> pedidosTemporales,
            List<Proveedor> proveedoresActivos,
            EstadisticasGeneracion estadisticas) {

        List<Integer> proveedoresUnitarios = pedidosTemporales.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .map(Map.Entry::getKey)
                .toList();

        List<Integer> proveedoresGrandes = pedidosTemporales.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (proveedoresUnitarios.isEmpty() || proveedoresGrandes.isEmpty()) {
            estadisticas.setPedidosUnitariosInevitables(proveedoresUnitarios.size());
            return;
        }

        List<Integer> proveedoresAEliminar = new ArrayList<>();

        for (Integer proveedorUnitarioId : proveedoresUnitarios) {
            List<AsignacionProducto> productos = pedidosTemporales.get(proveedorUnitarioId);
            AsignacionProducto productoUnico = productos.get(0);

            Integer mejorProveedorGrandeId = buscarMejorProveedorGrandeParaProducto(
                    productoUnico.getCodigoBarra(),
                    proveedoresGrandes,
                    proveedoresActivos
            );

            if (mejorProveedorGrandeId == null) {
                estadisticas.incrementarPedidosUnitariosInevitables();
                continue;
            }

            HistorialPrecio precioGrande = historialPrecioRepository.getCurrentPrice(
                    productoUnico.getCodigoBarra(),
                    mejorProveedorGrandeId
            );

            ProductoProveedor mappingGrande = productoProveedorRepository.findByProveedorAndProducto(
                    mejorProveedorGrandeId,
                    productoUnico.getCodigoBarra()
            );

            AsignacionProducto nuevaAsignacion = new AsignacionProducto(
                    productoUnico.getCodigoBarra(),
                    mappingGrande.getCodigoBarraProveedor(),
                    productoUnico.getCantidad(),
                    precioGrande.getPrecio(),
                    productoUnico.getProductoNombre()
            );

            pedidosTemporales.get(mejorProveedorGrandeId).add(nuevaAsignacion);
            proveedoresAEliminar.add(proveedorUnitarioId);
            estadisticas.incrementarPedidosConsolidados();
        }

        for (Integer proveedorId : proveedoresAEliminar) {
            pedidosTemporales.remove(proveedorId);
        }
    }

    private Integer buscarMejorProveedorGrandeParaProducto(
            Integer codigoBarra,
            List<Integer> proveedoresGrandes,
            List<Proveedor> proveedoresActivos) {

        Float mejorPrecio = Float.MAX_VALUE;
        Integer mejorProveedorId = null;

        for (Integer proveedorId : proveedoresGrandes) {
            ProductoProveedor productoProveedor =
                    productoProveedorRepository.findByProveedorAndProducto(proveedorId, codigoBarra);

            if (productoProveedor == null || productoProveedor.getEstado() != 1) {
                continue;
            }

            HistorialPrecio precioHistorial =
                    historialPrecioRepository.getCurrentPrice(codigoBarra, proveedorId);

            if (precioHistorial == null) {
                continue;
            }

            if (precioHistorial.getPrecio() < mejorPrecio) {
                mejorPrecio = precioHistorial.getPrecio();
                mejorProveedorId = proveedorId;
            }
        }

        return mejorProveedorId;
    }

    private List<PedidoResumen> crearPedidosFinales(
            Map<Integer, List<AsignacionProducto>> pedidosTemporales,
            List<Proveedor> proveedoresActivos,
            EstadisticasGeneracion estadisticas) {

        List<PedidoResumen> ordenesGeneradas = new ArrayList<>();

        for (Map.Entry<Integer, List<AsignacionProducto>> entry : pedidosTemporales.entrySet()) {
            Integer proveedorId = entry.getKey();
            List<AsignacionProducto> asignaciones = entry.getValue();

            String proveedorNombre = obtenerNombreProveedor(proveedorId, proveedoresActivos);
            Double rating = proveedoresActivos.stream()
                    .filter(p -> p.getId().equals(proveedorId))
                    .map(Proveedor::getRatingPromedio)
                    .findFirst()
                    .orElse(0.0);

            try {
                List<PedidoProducto> productos = asignaciones.stream()
                        .map(asig -> {
                            PedidoProducto pp = new PedidoProducto();
                            pp.setCodigoBarra(asig.getCodigoBarra());
                            pp.setCodigoBarraProveedor(asig.getCodigoBarraProveedor());
                            pp.setCantidad(asig.getCantidad());
                            return pp;
                        })
                        .toList();

                Float costoTotal = asignaciones.stream()
                        .map(AsignacionProducto::getCostoTotal)
                        .reduce(0.0f, Float::sum);

                Pedido nuevoPedido = new Pedido();
                nuevoPedido.setProveedorId(proveedorId);
                nuevoPedido.setEstadoId(1);
                nuevoPedido.setProductos(productos);

                Pedido pedidoCreado = pedidoService.createPedido(nuevoPedido);

                PedidoResumen resumen = new PedidoResumen();
                resumen.setPedidoId(pedidoCreado.getId());
                resumen.setProveedorNombre(proveedorNombre);
                resumen.setProveedorId(proveedorId);
                resumen.setCantidadProductos(asignaciones.size());
                resumen.setCostoTotal(costoTotal);
                resumen.setRatingProveedor(rating);

                ordenesGeneradas.add(resumen);
                estadisticas.incrementarTotalPedidosCreados();

            } catch (Exception e) {
                // Continuar con siguientes pedidos
            }
        }

        estadisticas.setTotalProveedoresUtilizados(ordenesGeneradas.size());
        return ordenesGeneradas;
    }

    private String obtenerNombreProveedor(Integer proveedorId, List<Proveedor> proveedoresActivos) {
        return proveedoresActivos.stream()
                .filter(p -> p.getId().equals(proveedorId))
                .map(Proveedor::getName)
                .findFirst()
                .orElse("Unknown Provider");
    }

    private Map<String, Object> buildSuccessResponse(
            String mensaje,
            List<PedidoResumen> ordenesGeneradas,
            List<Map<String, Object>> productosSinStock,
            EstadisticasGeneracion estadisticas) {

        Map<String, Object> response = new HashMap<>();
        response.put("exito", true);
        response.put("mensaje", mensaje);
        response.put("ordenesGeneradas", ordenesGeneradas);
        response.put("productosSinStock", productosSinStock);
        response.put("estadisticas", estadisticas.toMap());
        return response;
    }

    private Map<String, Object> buildErrorResponse(String mensaje, EstadisticasGeneracion estadisticas) {
        Map<String, Object> response = new HashMap<>();
        response.put("exito", false);
        response.put("mensaje", mensaje);
        response.put("ordenesGeneradas", List.of());
        response.put("productosSinStock", List.of());
        response.put("estadisticas", estadisticas.toMap());
        return response;
    }

    private List<PedidoProducto> prepararProductosPedido(List<Producto> productosBajos, Proveedor proveedor) {
        List<PedidoProducto> productosPedido = new ArrayList<>();

        for (Producto producto : productosBajos) {
            var productoProveedor = productoProveedorRepository.findByProveedorAndProducto(
                    proveedor.getId(), producto.getCodigoBarra());

            if (productoProveedor == null || productoProveedor.getEstado() != 1) {
                return new ArrayList<>();
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
