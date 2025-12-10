package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    Producto save(Producto producto);

    Producto update(Producto producto);

    Optional<Producto> findByBarCode(Integer barCode);

    List<Producto> findAll();

    void deleteByBarCode(Integer barCode);

    List<Producto> findByProviderId(Integer providerId);

    /**
     * Find products with stock below minimum (actualStock <= stockMinimo)
     * Used for automatic order generation
     * @return List of products that need restocking
     */
    List<Producto> findProductosBajoStock();
}
