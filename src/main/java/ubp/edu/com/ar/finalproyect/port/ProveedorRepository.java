package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository {

    Proveedor save(Proveedor proveedor);

    Optional<Proveedor> findById(Integer id);

    List<Proveedor> findAll();

    void deleteById(Integer id);
}
