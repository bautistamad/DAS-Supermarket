package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;

import java.util.List;
import java.util.Optional;

public interface HistorialPrecioRepository {
    List<HistorialPrecio> findByProducto(Integer codigoBarra);
    Optional<HistorialPrecio> findCurrentPrecioByProductoAndProveedor(Integer codigoBarra, Long idProveedor);
}
