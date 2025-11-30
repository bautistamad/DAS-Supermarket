package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Pedido;

import java.util.List;


public interface ProveedorIntegration {

    boolean checkHealth(String apiEndpoint, String clientId, String apiKey);

    List<Producto> getProductos(String apiEndpoint, String clientId, String apiKey);

    /**
     * Fetch provider's rating scale definitions
     * Called when creating a new provider to get their scale values
     */
    List<EscalaDefinicion> getEscala(String apiEndpoint, String clientId, String apiKey);

//    Pedido estimarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido);

//    Pedido asignarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido);

    Pedido consultarEstado(String apiEndpoint, String clientId, String apiKey, Integer idPedido);

    Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido);

//    boolean enviarEvaluacion(String apiEndpoint, String clientId, String apiKey,
//                            Integer pedidoId, Integer puntuacion);
}
