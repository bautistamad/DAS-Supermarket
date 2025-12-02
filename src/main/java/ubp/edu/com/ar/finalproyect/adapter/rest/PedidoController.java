package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.service.PedidoService;
import ubp.edu.com.ar.finalproyect.service.ProveedorIntegrationService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProveedorIntegrationService integrationService;

    public PedidoController(PedidoService pedidoService, ProveedorIntegrationService integrationService) {
        this.pedidoService = pedidoService;
        this.integrationService = integrationService;
    }

    // POST /api/pedidos - Create a new Pedido
    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        Pedido created = pedidoService.createPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/pedidos/{id} - Update an existing Pedido
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Integer id, @RequestBody Pedido pedido) {
        pedido.setId(id);
        Pedido updated = pedidoService.updatePedido(pedido);
        return ResponseEntity.ok(updated);
    }

    // GET /api/pedidos/{id} - Get order by id
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable Integer id) {
        Pedido pedido = pedidoService.getPedido(id);
        return ResponseEntity.ok(pedido);
    }

    // GET /api/pedidos - Get all pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> getAllpedidos() {
        List<Pedido> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    // DELETE /api/pedidos/{id} - Delete order by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/pedidos/proveedor/{providerId} - Get pedidos by provider
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<Pedido>> getPedidosByProvider(@PathVariable Integer proveedorId) {
        List<Pedido> pedidos = pedidoService.getPedidosByProveedor(proveedorId);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/{id}/productos - Get all products for a specific pedido
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<PedidoProducto>> getProductosByPedido(@PathVariable Integer id) {
        List<PedidoProducto> productos = pedidoService.getProductosByPedido(id);
        return ResponseEntity.ok(productos);
    }

    // POST /api/pedidos/{id}/asignar - Assign order to external provider
    @PostMapping("/{id}/asignar")
    public ResponseEntity<Pedido> asignarPedido(@PathVariable Integer id) {
        Pedido pedido = pedidoService.getPedido(id);

        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Assign order to provider
        Pedido pedidoAsignado = integrationService.asignarPedidoWithProveedor(
            pedido.getProveedorId(),
            pedido
        );

        if (pedidoAsignado == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        // Update local order with confirmed status
        pedidoAsignado.setId(id);
        Pedido updated = pedidoService.updatePedido(pedidoAsignado);

        return ResponseEntity.ok(updated);
    }

    // POST /api/pedidos/{id}/cancelar - Cancel an order with the external provider
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Integer id) {
        Pedido pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }
}
