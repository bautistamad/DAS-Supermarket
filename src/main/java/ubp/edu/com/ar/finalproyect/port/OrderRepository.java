package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Order update(Order order);
    Optional<Order> findById(Integer id);
    List<Order> findAll();
    void deleteById(Integer id);
    List<Order> findByProviderId(Integer providerId);
}
