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

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AutoPedidoService.class);

        logger.info("=== INICIO: Generación de pedido automático optimizado ===");

        List<Producto> productosBajos = productoRepository.findProductosBajoStock();
        estadisticas.setTotalProductosProcesados(productosBajos.size());
        logger.info("Productos con stock bajo encontrados: {}", productosBajos.size());

        if (productosBajos.isEmpty()) {
            logger.warn("No hay productos con stock bajo");
            return buildSuccessResponse("No hay productos con stock bajo", List.of(), List.of(), estadisticas);
        }

        List<Proveedor> proveedoresActivos = proveedorRepository.findAll().stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .toList();
        logger.info("Proveedores activos encontrados: {}", proveedoresActivos.size());

        if (proveedoresActivos.isEmpty()) {
            logger.error("No hay proveedores activos disponibles");
            return buildErrorResponse("No hay proveedores activos disponibles", estadisticas);
        }

        logger.info("Sincronizando precios de proveedores...");
        sincronizarPreciosProveedores(proveedoresActivos);

        logger.info("Asignando productos por mejor precio...");
        Map<Integer, List<AsignacionProducto>> pedidosTemporales =
                asignarProductosPorMejorPrecio(productosBajos, proveedoresActivos, estadisticas);
        logger.info("Pedidos temporales generados para {} proveedores", pedidosTemporales.size());

        if (pedidosTemporales.isEmpty()) {
            logger.error("Ningún proveedor puede suplir los productos necesarios");
            return buildErrorResponse("Ningún proveedor puede suplir los productos necesarios", estadisticas);
        }

        logger.info("Consolidando pedidos unitarios...");
        consolidarPedidosUnitarios(pedidosTemporales, proveedoresActivos, estadisticas);
        logger.info("Después de consolidar, quedan {} proveedores en pedidosTemporales", pedidosTemporales.size());

        logger.info("Creando pedidos finales...");
        List<PedidoResumen> ordenesGeneradas = crearPedidosFinales(pedidosTemporales, proveedoresActivos, estadisticas);
        logger.info("Pedidos creados: {}", ordenesGeneradas.size());

        logger.info("=== FIN: Generación exitosa ===");
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
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AutoPedidoService.class);
        List<OfertaProveedor> ofertas = new ArrayList<>();

        logger.debug("Buscando ofertas para producto: {} (codigo: {})", producto.getNombre(), producto.getCodigoBarra());

        for (Proveedor proveedor : proveedoresActivos) {
            ProductoProveedor productoProveedor =
                    productoProveedorRepository.findByProveedorAndProducto(proveedor.getId(), producto.getCodigoBarra());

            if (productoProveedor == null) {
                logger.debug("  - Proveedor {} NO tiene el producto asignado", proveedor.getName());
                continue;
            }

            if (productoProveedor.getEstado() != 1) {
                logger.debug("  - Proveedor {} tiene el producto pero estado != 1 (estado={})",
                    proveedor.getName(), productoProveedor.getEstado());
                continue;
            }

            HistorialPrecio precioHistorial =
                    historialPrecioRepository.getCurrentPrice(producto.getCodigoBarra(), proveedor.getId());

            if (precioHistorial == null) {
                logger.warn("  - Proveedor {} tiene el producto asignado pero SIN PRECIO en historial",
                    proveedor.getName());
                continue;
            }

            logger.info("  ✓ Proveedor {} tiene el producto con precio: ${}",
                proveedor.getName(), precioHistorial.getPrecio());

            OfertaProveedor oferta = new OfertaProveedor(
                    proveedor.getId(),
                    proveedor.getName(),
                    productoProveedor.getCodigoBarraProveedor(),
                    precioHistorial.getPrecio(),
                    proveedor.getRatingPromedio()
            );

            ofertas.add(oferta);
        }

        logger.info("Total ofertas encontradas para {}: {}", producto.getNombre(), ofertas.size());
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

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AutoPedidoService.class);
        logger.info("  Iniciando consolidación. Total proveedores antes: {}", pedidosTemporales.size());

        List<Integer> proveedoresUnitarios = pedidosTemporales.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .map(Map.Entry::getKey)
                .toList();

        List<Integer> proveedoresGrandes = pedidosTemporales.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        logger.info("  Proveedores unitarios (1 producto): {}", proveedoresUnitarios.size());
        logger.info("  Proveedores grandes (>1 producto): {}", proveedoresGrandes.size());

        if (proveedoresUnitarios.isEmpty() || proveedoresGrandes.isEmpty()) {
            logger.info("  No se puede consolidar (falta unitarios o grandes). Manteniendo todos los pedidos.");
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

            if (precioGrande == null) {
                estadisticas.incrementarPedidosUnitariosInevitables();
                continue;
            }

            ProductoProveedor mappingGrande = productoProveedorRepository.findByProveedorAndProducto(
                    mejorProveedorGrandeId,
                    productoUnico.getCodigoBarra()
            );

            if (mappingGrande == null) {
                estadisticas.incrementarPedidosUnitariosInevitables();
                continue;
            }

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

        logger.info("  Consolidación completada. Proveedores eliminados: {}, Proveedores restantes: {}",
                proveedoresAEliminar.size(), pedidosTemporales.size());
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

        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AutoPedidoService.class);
        List<PedidoResumen> ordenesGeneradas = new ArrayList<>();

        logger.info("  crearPedidosFinales: Recibido map con {} entradas", pedidosTemporales.size());
        logger.info("  crearPedidosFinales: entrySet size = {}", pedidosTemporales.entrySet().size());

        for (Map.Entry<Integer, List<AsignacionProducto>> entry : pedidosTemporales.entrySet()) {
            logger.info("  DENTRO DEL FOR LOOP - procesando entry");

            Integer proveedorId = entry.getKey();
            List<AsignacionProducto> asignaciones = entry.getValue();
            String proveedorNombre = "Proveedor-" + proveedorId;

            try {

                logger.info("  Proveedor ID: {}, Asignaciones: {}", proveedorId, asignaciones.size());

                proveedorNombre = obtenerNombreProveedor(proveedorId, proveedoresActivos);
                logger.info("  Nombre proveedor: {}", proveedorNombre);

                Double rating = proveedoresActivos.stream()
                        .filter(p -> p.getId().equals(proveedorId))
                        .map(p -> p.getRatingPromedio() != null ? p.getRatingPromedio() : 0.0)
                        .findFirst()
                        .orElse(0.0);

                logger.info("Creando pedido para proveedor {} con {} productos", proveedorNombre, asignaciones.size());

                List<PedidoProducto> productos = asignaciones.stream()
                        .map(asig -> {
                            PedidoProducto pp = new PedidoProducto();
                            pp.setCodigoBarra(asig.getCodigoBarra());
                            pp.setCodigoBarraProveedor(asig.getCodigoBarraProveedor());
                            pp.setCantidad(asig.getCantidad());
                            logger.debug("  - Producto: codigoBarra={}, cantidad={}", asig.getCodigoBarra(), asig.getCantidad());
                            return pp;
                        })
                        .toList();

                Float costoTotal = asignaciones.stream()
                        .map(AsignacionProducto::getCostoTotal)
                        .reduce(0.0f, Float::sum);

                logger.info("Costo total del pedido: ${}", costoTotal);

                Pedido nuevoPedido = new Pedido();
                nuevoPedido.setProveedorId(proveedorId);
                nuevoPedido.setEstadoId(1);
                nuevoPedido.setProductos(productos);
                // Set estimated date to 7 days from now for automatic orders
                nuevoPedido.setFechaEstimada(java.time.LocalDateTime.now().plusDays(7));

                logger.info("Llamando a pedidoService.createPedido...");
                Pedido pedidoCreado = pedidoService.createPedido(nuevoPedido);
                logger.info("Pedido creado exitosamente con ID: {}", pedidoCreado.getId());

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
                logger.error("ERROR creando pedido para proveedor {}: {}", proveedorNombre, e.getMessage(), e);
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
