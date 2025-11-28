package ubp.edu.com.ar.finalproyect.adapter.external.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
    public Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        logger.warn("SOAP adapter stub called for cancelarPedido. WSDL: {}, clientId: {}, pedidoId: {}",
            apiEndpoint, clientId, idPedido);
        logger.info("Returning null - SOAP adapter not yet implemented");
        return null;
    }

}
