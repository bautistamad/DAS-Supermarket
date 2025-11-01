package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findByBarCode(Integer barCode);
    List<Product> findAll();
    void deleteByBarCode(Integer barCode);
}
