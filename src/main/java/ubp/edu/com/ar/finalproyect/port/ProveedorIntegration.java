package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Pedido;

import java.util.List;
import java.util.Map;


public interface ProveedorIntegration {

    boolean checkHealth(String apiEndpoint, String clientId, String apiKey);

    List<Producto> getProductos(String apiEndpoint, String clientId, String apiKey);

    /**
     * Fetch provider's rating scale definitions
     * Called when creating a new provider to get their scale values
     */
    List<EscalaDefinicion> getEscala(String apiEndpoint, String clientId, String apiKey);

    /**
     * Assign order to provider (confirm order creation)
     * @return Pedido with provider's order ID and confirmation details
     */
    Pedido asignarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido);

    Pedido consultarEstado(String apiEndpoint, String clientId, String apiKey, Integer idPedido);

    Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido);

    /**
     * Send order evaluation to provider
     * @param apiEndpoint Provider's API endpoint
     * @param clientId Client authentication ID
     * @param apiKey Client API key
     * @param pedidoId Order ID to rate
     * @param puntuacion Rating value (in provider's scale)
     * @return true if evaluation was successfully sent, false otherwise
     */
    boolean enviarEvaluacion(String apiEndpoint, String clientId, String apiKey,
                            Integer pedidoId, Integer puntuacion);

    /**
     * Estimate order price from provider
     * @param apiEndpoint Provider's API endpoint
     * @param clientId Client authentication ID
     * @param apiKey Client API key
     * @param pedido Order with products to estimate
     * @return Map with estimation details (precioEstimadoTotal, fechaEstimada, productosJson)
     */
    Map<String, Object> estimarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido);
}
