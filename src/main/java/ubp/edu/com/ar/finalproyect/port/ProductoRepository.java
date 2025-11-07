package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto save(Producto producto);

    Optional<Producto> findByBarCode(Integer barCode);

    List<Producto> findAll();

    void deleteByBarCode(Integer barCode);

    List<Producto> findByProviderId(Integer providerId);
}
