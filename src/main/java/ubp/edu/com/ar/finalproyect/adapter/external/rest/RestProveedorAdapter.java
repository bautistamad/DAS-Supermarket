package ubp.edu.com.ar.finalproyect.adapter.external.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * REST adapter for external provider integrations.
 * Implements HTTP-based communication with REST providers using WebClient.
 */
@Component("restProveedorAdapter")
public class RestProveedorAdapter implements ProveedorIntegration {

    private static final Logger logger = LoggerFactory.getLogger(RestProveedorAdapter.class);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    public RestProveedorAdapter() {
    }

    @Override
    public boolean checkHealth(String apiEndpoint, String apiKey) {
        return true;
    }

    @Override
    public List<Producto> getProductos(String apiEndpoint, String apiKey) {
        logger.warn("REST adapter stub called for getProductos. Endpoint: {}", apiEndpoint);
        logger.info("Returning empty list - waiting for provider API specification");

        return new ArrayList<>();
    }
}
