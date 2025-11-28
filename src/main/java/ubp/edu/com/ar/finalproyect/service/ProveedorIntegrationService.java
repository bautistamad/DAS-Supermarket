package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.ProveedorNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.util.List;

@Service
public class ProveedorIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorIntegrationService.class);

    private final ProveedorRepository proveedorRepository;
    private final ProveedorIntegrationFactory factory;

    public ProveedorIntegrationService(ProveedorRepository proveedorRepository,
                                       ProveedorIntegrationFactory factory) {
        this.proveedorRepository = proveedorRepository;
        this.factory = factory;
    }

    /**
     * Check health for an existing provider (by ID)
     * Fetches provider from database and validates connection
     */
    public boolean checkProveedorHealth(Integer proveedorId) {
        logger.info("Checking health for provider ID: {}", proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        return checkProveedorHealthDirect(
            proveedor.getApiEndpoint(),
            proveedor.getApiKey(),
            proveedor.getTipoServicio()
        );
    }

    /**
     * Check health for a provider using direct parameters (without DB lookup)
     * Useful for validating provider before creation
     */
    public boolean checkProveedorHealthDirect(String apiEndpoint, String apiKey, Integer tipoServicio) {
        logger.info("Checking health for provider endpoint: {}", apiEndpoint);

        try {
            ProveedorIntegration adapter = factory.getAdapter(tipoServicio);
            boolean isHealthy = adapter.checkHealth(apiEndpoint, apiKey);

            logger.info("Provider health check for endpoint {}: {}", apiEndpoint, isHealthy ? "OK" : "KO");
            return isHealthy;

        } catch (Exception e) {
            logger.error("Failed to check health for endpoint: {}", apiEndpoint, e);
            return false;
        }
    }


    private Proveedor getProveedor(Integer proveedorId) {
        return proveedorRepository.findById(proveedorId)
            .orElseThrow(() -> new ProveedorNotFoundException(
                "Provider with ID " + proveedorId + " not found"
            ));
    }
}
