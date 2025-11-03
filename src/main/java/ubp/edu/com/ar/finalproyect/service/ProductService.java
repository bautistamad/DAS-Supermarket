package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Product;
import ubp.edu.com.ar.finalproyect.exception.ProductNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(Product product) {
        // Validate input

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getBarCode() == null) {
            throw new IllegalArgumentException("Product barCode cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        return repository.save(product);
    }

    public Product getProduct(Integer barCode) {
        return repository.findByBarCode(barCode)
            .orElseThrow(() -> new ProductNotFoundException(barCode));
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public void deleteProduct(Integer barCode) {
        if (barCode == null) {
            throw new IllegalArgumentException("BarCode cannot be null");
        }
        // Check if product exists before deleting
        repository.findByBarCode(barCode)
            .orElseThrow(() -> new ProductNotFoundException(barCode));

        repository.deleteByBarCode(barCode);
    }

    public List<Product> getProductByProvider(Integer providerId) {
        if (providerId == null) {
            throw new IllegalArgumentException("Provider ID cannot be null");
        }

        return repository.findByProviderId(providerId);
    }

}
