package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Order;
import ubp.edu.com.ar.finalproyect.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /api/orders - Create a new Order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/orders/{id} - Update an existing Order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        order.setId(id);
        Order updated = orderService.updateOrder(order);
        return ResponseEntity.ok(updated);
    }

    // GET /api/orders/{id} - Get order by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Integer id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    // GET /api/orders - Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // DELETE /api/orders/{id} - Delete order by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/orders/provider/{providerId} - Get orders by provider
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Order>> getOrdersByProvider(@PathVariable Integer providerId) {
        List<Order> orders = orderService.getOrdersByProvider(providerId);
        return ResponseEntity.ok(orders);
    }
}
