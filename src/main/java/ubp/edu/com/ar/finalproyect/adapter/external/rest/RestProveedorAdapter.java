package ubp.edu.com.ar.finalproyect.adapter.external.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.*;
import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;
import ubp.edu.com.ar.finalproyect.utils.Httpful;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("restProveedorAdapter")
public class RestProveedorAdapter implements ProveedorIntegration {

    private static final Logger logger = LoggerFactory.getLogger(RestProveedorAdapter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RestProveedorAdapter() {
    }

    @Override
    public boolean checkHealth(String apiEndpoint, String apiKey) {
        try {
            logger.info("Checking health for provider: {}", apiEndpoint);

            // Parse apiKey format: "clientId:apikey"
            String[] apiKeyParts = apiKey.split(":", 2);
            if (apiKeyParts.length != 2) {
                logger.error("Invalid API key format. Expected 'clientId:apikey', got: {}", apiKey);
                return false;
            }

            String clientId = apiKeyParts[0];
            String actualApiKey = apiKeyParts[1];

            logger.info("Sending health check with clientId: {}, apikey: {}", clientId, actualApiKey);

            HealthResponse response = new Httpful(apiEndpoint)
                    .path("/api/health")
                    .addQueryParam("clientId", clientId)
                    .addQueryParam("apikey", actualApiKey)
                    .get()
                    .execute(HealthResponse.class);

            logger.info("Health check result: {}", response.getStatus());
            boolean isHealthy = response.isHealthy();

            if (!isHealthy) {
                logger.error("Provider returned unhealthy status. Response: {}", response.getStatus());
            }

            return isHealthy;
        } catch (Exception e) {
            logger.error("Health check failed for endpoint: {}", apiEndpoint, e);
            return false;
        }
    }
}
