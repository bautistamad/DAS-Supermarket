package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.ConsultarEstado;
import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Pedido;

import java.util.List;
import java.util.Map;


public interface ProveedorIntegration {

    boolean checkHealth(String apiEndpoint, String clientId, String apiKey);

    List<Producto> getProductos(String apiEndpoint, String clientId, String apiKey);

    List<EscalaDefinicion> getEscala(String apiEndpoint, String clientId, String apiKey);

    Pedido asignarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido);

    ConsultarEstado consultarEstado(String apiEndpoint, String clientId, String apiKey, Integer idPedido);

    Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido);

    boolean enviarEvaluacion(String apiEndpoint, String clientId, String apiKey,
                            Integer pedidoId, Integer puntuacion);

    Map<String, Object> estimarPedido(String apiEndpoint, String clientId, String apiKey);
}
