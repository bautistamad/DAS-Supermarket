package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Pedido;

import java.util.List;

/**
 * Port interface for external provider integrations (REST/SOAP)
 * Defines the contract for communicating with external providers
 * Works with domain entities, not DTOs (DTOs are adapter implementation details)
 */
public interface ProveedorIntegration {

    boolean checkHealth(String apiEndpoint, String apiKey);

//    List<Producto> getProductos(String apiEndpoint, String apiKey);

//    Pedido estimarPedido(String apiEndpoint, String apiKey, Pedido pedido);
//
//    Pedido asignarPedido(String apiEndpoint, String apiKey, Pedido pedido);
//
//    Pedido consultarEstado(String apiEndpoint, String apiKey, Integer idPedido);
//
//    Pedido cancelarPedido(String apiEndpoint, String apiKey, Integer idPedido);
//
}
