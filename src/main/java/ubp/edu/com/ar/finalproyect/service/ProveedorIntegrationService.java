package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.domain.Producto;
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

    public boolean checkProveedorHealth(Integer proveedorId) {
        logger.info("Checking health for provider ID: {}", proveedorId);

        // Fetch provider details
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
            .orElseThrow(() -> new ProveedorNotFoundException(
                "Provider with ID " + proveedorId + " not found"
            ));

        // Select appropriate adapter based on service type
        ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
        logger.debug("Selected adapter for service type: {}", proveedor.getTipoServicio());

        // Check health
        try {
            boolean isHealthy = adapter.checkHealth(
                proveedor.getApiEndpoint(),
                proveedor.getApiKey()
            );

            if (isHealthy) {
                logger.info("Provider ID {} is healthy", proveedorId);
            } else {
                logger.warn("Provider ID {} health check failed", proveedorId);
            }

            return isHealthy;

        } catch (Exception e) {
            logger.error("Failed to check health for provider ID: {}", proveedorId, e);
            return false;
        }
    }

    public List<Producto> syncProductosFromProveedor(Integer proveedorId) {
        logger.info("Starting product sync for provider ID: {}", proveedorId);

        // 1. Fetch provider details
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
            .orElseThrow(() -> new ProveedorNotFoundException(
                "Provider with ID " + proveedorId + " not found"
            ));

        // 3. Select appropriate adapter based on service type
        ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
        logger.debug("Selected adapter for service type: {}", proveedor.getTipoServicio());

        // 4. Fetch products from external API
        try {
            List<Producto> productos = adapter.getProductos(
                proveedor.getApiEndpoint(),
                proveedor.getApiKey()
            );

            logger.info("Successfully fetched {} products from provider ID: {}",
                productos.size(), proveedorId);

            return productos;

        } catch (Exception e) {
            logger.error("Failed to sync products from provider ID: {}", proveedorId, e);
            throw new RuntimeException(
                "Error syncing products from provider " + proveedor.getName(), e
            );
        }
    }
}
