package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Product;
import ubp.edu.com.ar.finalproyect.port.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Optional<Product> getProduct(Integer barCode) {
        return repository.findByBarCode(barCode);
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public void deleteProduct(Integer barCode) {
        repository.deleteByBarCode(barCode);
    }

}
