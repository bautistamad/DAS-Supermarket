package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Order;
import ubp.edu.com.ar.finalproyect.exception.OrderNotFoundException;
import ubp.edu.com.ar.finalproyect.port.OrderRepository;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(Order order) {
        // Validate input
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getEstadoId() == null) {
            throw new IllegalArgumentException("Order estado cannot be null");
        }
        if (order.getProveedorId() == null) {
            throw new IllegalArgumentException("Order proveedor cannot be null");
        }

        return repository.save(order);
    }

    public Order updateOrder(Order order) {
        // Validate input
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("Order id cannot be null");
        }
        if (order.getEstadoId() == null) {
            throw new IllegalArgumentException("Order estado cannot be null");
        }

        // Check if order exists
        repository.findById(order.getId())
                .orElseThrow(() -> new OrderNotFoundException(order.getId()));

        return repository.update(order);
    }

    public Order getOrder(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Order id cannot be null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public void deleteOrder(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Order id cannot be null");
        }
        repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        repository.deleteById(id);
    }

    public List<Order> getOrdersByProvider(Integer providerId) {
        if (providerId == null) {
            throw new IllegalArgumentException("Provider id cannot be null");
        }
        return repository.findByProviderId(providerId);
    }
}
