package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.exception.ProductoNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Producto createProducto(Producto producto) {
        // Validate input

        if (producto == null) {
            throw new IllegalArgumentException("Producto cannot be null");
        }
        if (producto.getBarCode() == null) {
            throw new IllegalArgumentException("Producto barCode cannot be null");
        }
        if (producto.getName() == null || producto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Producto name cannot be null or empty");
        }

        return repository.save(producto);
    }

    public Producto getProducto(Integer barCode) {
        return repository.findByBarCode(barCode)
            .orElseThrow(() -> new ProductoNotFoundException(barCode));
    }

    public List<Producto> getAllProductos() {
        return repository.findAll();
    }

    public void deleteProducto(Integer barCode) {
        if (barCode == null) {
            throw new IllegalArgumentException("BarCode cannot be null");
        }
        // Check if product exists before deleting
        repository.findByBarCode(barCode)
            .orElseThrow(() -> new ProductoNotFoundException(barCode));

        repository.deleteByBarCode(barCode);
    }

    public List<Producto> getProductoByProveedor(Integer providerId) {
        if (providerId == null) {
            throw new IllegalArgumentException("Proveedor ID cannot be null");
        }

        return repository.findByProviderId(providerId);
    }

}
