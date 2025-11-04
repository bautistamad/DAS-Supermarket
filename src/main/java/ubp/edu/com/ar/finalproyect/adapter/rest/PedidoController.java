package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
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
    public ResponseEntity<Pedido> getOrder(@PathVariable Integer id) {
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

    // GET /api/pedidos/provider/{providerId} - Get pedidos by provider
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<Pedido>> getpedidosByProvider(@PathVariable Integer proveedorId) {
        List<Pedido> pedidos = pedidoService.getPedidosByProveedor(proveedorId);
        return ResponseEntity.ok(pedidos);
    }
}
