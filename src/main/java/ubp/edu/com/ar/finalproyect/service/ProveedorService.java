package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.ProveedorNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public Proveedor createProveedor(Proveedor proveedor) {
        // Validate input
        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor cannot be null");
        }
        if (proveedor.getName() == null || proveedor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Proveedor name cannot be null or empty");
        }

        return repository.save(proveedor);
    }

    public Optional<Proveedor> getProveedor(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Proveedor ID cannot be null");
        }
        return repository.findById(id);
    }

    public List<Proveedor> getAllProveedores() {
        return repository.findAll();
    }

    public void deleteProveedor(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Proveedor ID cannot be null");
        }

        // Check if provider exists before deleting
        repository.findById(id)
            .orElseThrow(() -> new ProveedorNotFoundException(id));

        repository.deleteById(id);
    }
}
