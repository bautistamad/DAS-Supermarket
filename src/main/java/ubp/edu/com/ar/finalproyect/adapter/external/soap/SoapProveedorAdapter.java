package ubp.edu.com.ar.finalproyect.adapter.external.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;

import java.util.ArrayList;
import java.util.List;

@Component("soapProveedorAdapter")
public class SoapProveedorAdapter implements ProveedorIntegration {

    private static final Logger logger = LoggerFactory.getLogger(SoapProveedorAdapter.class);

    @Override
    public boolean checkHealth(String apiEndpoint, String clientId, String apiKey) {
        logger.warn("SOAP adapter stub called for checkHealth. WSDL: {}, clientId: {}", apiEndpoint, clientId);
        logger.info("Returning false - SOAP adapter not yet implemented");
        return false;
    }

    @Override
    public List<Producto> getProductos(String apiEndpoint, String clientId, String apiKey) {
        logger.warn("SOAP adapter stub called for getProductos. WSDL: {}, clientId: {}", apiEndpoint, clientId);
        logger.info("Returning empty list - SOAP adapter not yet implemented");
        return new ArrayList<>();
    }

    @Override
    public List<EscalaDefinicion> getEscala(String apiEndpoint, String clientId, String apiKey) {
        logger.warn("SOAP adapter stub called for getEscala. WSDL: {}, clientId: {}", apiEndpoint, clientId);
        logger.info("Returning empty list - SOAP adapter not yet implemented");
        return new ArrayList<>();
    }

    @Override
    public Pedido asignarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido) {
        logger.warn("SOAP adapter stub called for asignarPedido. WSDL: {}, clientId: {}", apiEndpoint, clientId);
        logger.info("Returning null - SOAP adapter not yet implemented");
        return null;
    }

    @Override
    public Pedido consultarEstado(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        logger.warn("SOAP adapter stub called for consultarEstado. WSDL: {}, clientId: {}, pedidoId: {}",
            apiEndpoint, clientId, idPedido);
        logger.info("Returning null - SOAP adapter not yet implemented");
        return null;
    }

    @Override
    public Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        logger.warn("SOAP adapter stub called for cancelarPedido. WSDL: {}, clientId: {}, pedidoId: {}",
            apiEndpoint, clientId, idPedido);
        logger.info("Returning null - SOAP adapter not yet implemented");
        return null;
    }

}
