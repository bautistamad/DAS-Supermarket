package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.domain.Producto;

import java.util.List;


public interface ProveedorIntegration {

    boolean checkHealth(String apiEndpoint, String apiKey);

    List<Producto> getProductos(String apiEndpoint, String apiKey);


}
