package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.ProductoProveedor;
import ubp.edu.com.ar.finalproyect.exception.ProductoNotFoundException;
import ubp.edu.com.ar.finalproyect.exception.ProveedorNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProductoProveedorRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

@Service
public class ProductoProveedorService {

    private final ProductoProveedorRepository productoProveedorRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoProveedorService(ProductoProveedorRepository productoProveedorRepository,
                                   ProductoRepository productoRepository,
                                   ProveedorRepository proveedorRepository) {
        this.productoProveedorRepository = productoProveedorRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional
    public ProductoProveedor assignProductToProvider(Integer codigoBarra, Integer idProveedor,
                                                     Integer codigoBarraProveedor, Integer estado) {
        // Validate producto exists
        Producto producto = productoRepository.findByBarCode(codigoBarra)
                .orElseThrow(() -> new ProductoNotFoundException(codigoBarra));

        // Validate proveedor exists
        proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNotFoundException(idProveedor));

        // Validate estado (1=Disponible, 2=Agotado, 3=Descontinuado)
        if (estado == null || estado < 1 || estado > 3) {
            throw new IllegalArgumentException("Estado must be between 1 and 3");
        }

        // Set product estado to 2 (Agotado) when assigning to provider
        producto.setEstadoId(2);
        productoRepository.save(producto);

        // Create assignment
        ProductoProveedor assignment = new ProductoProveedor(
                idProveedor,
                codigoBarra,
                codigoBarraProveedor
        );

        return productoProveedorRepository.assign(assignment);
    }

    @Transactional
    public void unassignProductFromProvider(Integer codigoBarra, Integer idProveedor) {
        // Validate producto exists
        productoRepository.findByBarCode(codigoBarra)
                .orElseThrow(() -> new ProductoNotFoundException(codigoBarra));

        // Validate proveedor exists
        proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNotFoundException(idProveedor));

        productoProveedorRepository.unassign(codigoBarra, idProveedor);
    }
}
