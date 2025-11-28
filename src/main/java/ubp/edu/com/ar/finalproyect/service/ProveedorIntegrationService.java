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
            proveedor.getClientId(),
            proveedor.getApiKey(),
            proveedor.getTipoServicio()
        );
    }

    public boolean checkProveedorHealthDirect(String apiEndpoint, String clientId, String apiKey, Integer tipoServicio) {
        logger.info("Checking health for provider endpoint: {}", apiEndpoint);

        try {
            ProveedorIntegration adapter = factory.getAdapter(tipoServicio);
            boolean isHealthy = adapter.checkHealth(apiEndpoint, clientId, apiKey);

            logger.info("Provider health check for endpoint {}: {}", apiEndpoint, isHealthy ? "OK" : "KO");
            return isHealthy;

        } catch (Exception e) {
            logger.error("Failed to check health for endpoint: {}", apiEndpoint, e);
            return false;
        }
    }



    public Pedido cancelarPedidoWithProveedor(Integer proveedorId, Integer pedidoId) {
        logger.info("Attempting to cancel order {} with provider ID: {}", pedidoId, proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        try {
            ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
            Pedido pedidoCancelado = adapter.cancelarPedido(
                proveedor.getApiEndpoint(),
                proveedor.getClientId(),
                proveedor.getApiKey(),
                pedidoId
            );

            if (pedidoCancelado != null) {
                logger.info("Successfully cancelled order {} with provider {}", pedidoId, proveedorId);
            } else {
                logger.warn("Failed to cancel order {} with provider {}. Provider did not confirm cancellation.",
                    pedidoId, proveedorId);
            }

            return pedidoCancelado;

        } catch (Exception e) {
            logger.error("Exception occurred while cancelling order {} with provider {}",
                pedidoId, proveedorId, e);
            return null;
        }
    }

    private Proveedor getProveedor(Integer proveedorId) {
        return proveedorRepository.findById(proveedorId)
            .orElseThrow(() -> new ProveedorNotFoundException(
                "Provider with ID " + proveedorId + " not found"
            ));
    }
}
