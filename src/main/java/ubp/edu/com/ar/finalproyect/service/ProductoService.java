package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.exception.ProductoNotFoundException;
import ubp.edu.com.ar.finalproyect.port.HistorialPrecioRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final HistorialPrecioRepository historialPrecioRepository;

    public ProductoService(ProductoRepository repository, HistorialPrecioRepository historialPrecioRepository) {
        this.repository = repository;
        this.historialPrecioRepository =historialPrecioRepository;
    }

    public Producto createProducto(Producto producto) {
        // Validate input

        if (producto == null) {
            throw new IllegalArgumentException("Producto cannot be null");
        }
        if (producto.getCodigoBarra() == null) {
            throw new IllegalArgumentException("Producto barCode cannot be null");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Producto name cannot be null or empty");
        }

        return repository.save(producto);
    }

    public Producto getProducto(Integer barCode, Boolean priceHistory) {

        Producto product = new Producto();
        product = repository.findByBarCode(barCode)
            .orElseThrow(() -> new ProductoNotFoundException(barCode));

        if (priceHistory) {
            product.setPrecios(historialPrecioRepository.findByProducto(barCode));
        }

        return product;
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

    public List<Producto> getProductoByProveedor(Integer providerId, Boolean priceHistory) {
        if (providerId == null) {
            throw new IllegalArgumentException("Proveedor ID cannot be null");
        }

        List<Producto> productos = repository.findByProviderId(providerId);

        if (priceHistory != null && priceHistory) {
            productos.forEach(producto -> {
                producto.setPrecios(historialPrecioRepository.findByProducto(producto.getCodigoBarra()));
            });
        }

        return productos;
    }

}
