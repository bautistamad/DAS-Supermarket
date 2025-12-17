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
import ubp.edu.com.ar.finalproyect.domain.subdomain.OfertaProveedor;
import ubp.edu.com.ar.finalproyect.port.HistorialPrecioRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoProveedorRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Map<String, Object> generarPedidoAutomaticoOptimizado() {
        List<Producto> productosBajos = productoRepository.findProductosBajoStock();

        List<Proveedor> proveedoresActivos = proveedorRepository.findAll().stream()
                .filter(p -> p.getActivo() == true)
                .toList();

        sincronizarPrecios(proveedoresActivos);

        Map<Integer, List<AsignacionProducto>> pedidosTemporales = asignacionProductoMenorPrecio(productosBajos, proveedoresActivos);

        if (pedidosTemporales.isEmpty()) {
            return buildResponse(false, "No hay proveedores disponibles para los productos", 0);
        }

        agrupacionPedidos(pedidosTemporales, proveedoresActivos);
        int totalPedidos = crearPedidosFinales(pedidosTemporales);

        return buildResponse(true, "Proceso finalizado con éxito", totalPedidos);
    }

    // Creacion / Agrupar Pedidos
    private Map<Integer, List<AsignacionProducto>> asignacionProductoMenorPrecio(
            List<Producto> productos, List<Proveedor> proveedores) {

        Map<Integer, List<AsignacionProducto>> mapaPedidos = new HashMap<>();

        for (Producto prod : productos) {
            OfertaProveedor mejorOferta = buscarMejorOferta(prod, proveedores);

            if (mejorOferta != null) {
                AsignacionProducto item = new AsignacionProducto(
                        prod.getCodigoBarra(),
                        mejorOferta.getCodigoBarraProveedor(),
                        prod.getMaxStock() - prod.getActualStock(),
                        mejorOferta.getPrecio(),
                        prod.getNombre()
                );

                mapaPedidos
                        .computeIfAbsent(mejorOferta.getProveedorId(), k -> new ArrayList<>())
                        .add(item);
            }
        }
        return mapaPedidos;
    }

    private OfertaProveedor buscarMejorOferta(Producto producto, List<Proveedor> proveedores) {
        OfertaProveedor mejor = null;
        for (Proveedor p : proveedores) {
            HistorialPrecio precio = historialPrecioRepository.getCurrentPrice(producto.getCodigoBarra(), p.getId());
            ProductoProveedor pp = productoProveedorRepository.findByProveedorAndProducto(p.getId(), producto.getCodigoBarra());

            if (precio != null && pp != null) {
                OfertaProveedor actual = new OfertaProveedor(
                        p.getId(), p.getName(), pp.getCodigoBarraProveedor(),
                        precio.getPrecio(), p.getRatingPromedio());

                if (esMejor(actual, mejor)) mejor = actual;
            }
        }
        return mejor;
    }

    private boolean esMejor(OfertaProveedor actual, OfertaProveedor mejor) {
        if (mejor == null) return true;
        if (actual.getPrecio() < mejor.getPrecio()) return true;
        return actual.getPrecio().equals(mejor.getPrecio()) && actual.getRating() > mejor.getRating();
    }

    private void sincronizarPrecios(List<Proveedor> proveedores) {
        proveedores.forEach(p -> {
            try { proveedorIntegrationService.syncProductosFromProveedor(p.getId()); } catch (Exception e) {}
        });
    }

    private Map<String, Object> buildResponse(boolean exito, String mensaje, int cantidad) {
        Map<String, Object> response = new HashMap<>();
        response.put("exito", exito);
        response.put("mensaje", mensaje);
        return response;
    }

    // Post Procesado Pedidos

    private void agrupacionPedidos(Map<Integer, List<AsignacionProducto>> pedidos, List<Proveedor> proveedoresActivos) {

        List<Integer> proveedoresUnitarios = pedidos.entrySet().stream()
                .filter(e -> e.getValue().size() == 1).map(Map.Entry::getKey).toList();

        List<Integer> proveedoresGrandes = pedidos.entrySet().stream()
                .filter(e -> e.getValue().size() > 1).map(Map.Entry::getKey).toList();

        if (proveedoresUnitarios.isEmpty() || proveedoresGrandes.isEmpty()) {
            return;
        }

        // Iterar sobre los pequeños para intentar moverlos
        List<Integer> aEliminar = new ArrayList<>();

        for (Integer idUnitario : proveedoresUnitarios) {
            AsignacionProducto itemUnitario = pedidos.get(idUnitario).get(0);

            // Buscar si algún proveedor grande vende este producto
            OfertaProveedor mejorOpcionGrande = null;

            for (Integer idGrande : proveedoresGrandes) {
                // Consultamos precio en DB para este proveedor grande
                HistorialPrecio precioGrande = historialPrecioRepository.getCurrentPrice(itemUnitario.getCodigoBarra(), idGrande);
                ProductoProveedor ppGrande = productoProveedorRepository.findByProveedorAndProducto(idGrande, itemUnitario.getCodigoBarra());

                if (precioGrande != null && ppGrande != null) {
                    // Encontramos que un proveedor grande si lo vende
                    if (mejorOpcionGrande == null || precioGrande.getPrecio() < mejorOpcionGrande.getPrecio()) {
                        // Buscamos el nombre/rating solo si es necesario o usamos dummy data si no afecta logica
                        mejorOpcionGrande = new OfertaProveedor(
                                idGrande, "Consolidacion", ppGrande.getCodigoBarraProveedor(),
                                precioGrande.getPrecio(), 0.0);
                    }
                }
            }

            // Si encontramos un proveedor para el pedido viejo
            if (mejorOpcionGrande != null) {

                AsignacionProducto itemReasignado = new AsignacionProducto(
                        itemUnitario.getCodigoBarra(),
                        mejorOpcionGrande.getCodigoBarraProveedor(),
                        itemUnitario.getCantidad(),
                        mejorOpcionGrande.getPrecio(),
                        itemUnitario.getProductoNombre()
                );

                pedidos.get(mejorOpcionGrande.getProveedorId()).add(itemReasignado);
                aEliminar.add(idUnitario);
            }
        }

        aEliminar.forEach(pedidos::remove);
    }

    // Creacion pedidos
    private int crearPedidosFinales(Map<Integer, List<AsignacionProducto>> pedidosMap) {
        int count = 0;
        for (var entry : pedidosMap.entrySet()) {
            Pedido pedido = new Pedido();
            pedido.setProveedorId(entry.getKey());
            pedido.setEstadoId(1);
            pedido.setFechaEstimada(LocalDateTime.now().plusDays(7));

            List<PedidoProducto> lineas = entry.getValue().stream().map(a -> {
                PedidoProducto pp = new PedidoProducto();
                pp.setCodigoBarra(a.getCodigoBarra());
                pp.setCodigoBarraProveedor(a.getCodigoBarraProveedor());
                pp.setCantidad(a.getCantidad());
                return pp;
            }).collect(Collectors.toList());

            pedido.setProductos(lineas);
            pedidoService.createPedido(pedido);
            count++;
        }
        return count;
    }
}
