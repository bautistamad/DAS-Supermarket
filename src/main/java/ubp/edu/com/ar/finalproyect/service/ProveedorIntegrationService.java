package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.ProveedorNotFoundException;
import ubp.edu.com.ar.finalproyect.port.HistorialPrecioRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProveedorIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorIntegrationService.class);

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final HistorialPrecioRepository historialPrecioRepository;
    private final ProveedorIntegrationFactory factory;

    public ProveedorIntegrationService(ProveedorRepository proveedorRepository,
                                       ProductoRepository productoRepository,
                                       HistorialPrecioRepository historialPrecioRepository,
                                       ProveedorIntegrationFactory factory) {
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.historialPrecioRepository = historialPrecioRepository;
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

    /**
     * Synchronize prices from an external provider
     * Fetches products from provider and updates prices in our database
     */
    @Transactional
    public Map<String, Integer> syncProductosFromProveedor(Integer proveedorId) {
        logger.info("Starting price sync for provider ID: {}", proveedorId);

        Map<String, Integer> result = new HashMap<>();
        result.put("pricesCreated", 0);
        result.put("pricesUpdated", 0);
        result.put("errors", 0);

        Proveedor proveedor = getProveedor(proveedorId);

        // 1. Get products from provider
        ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
        List<Producto> productosProveedor = adapter.getProductos(
                proveedor.getApiEndpoint(),
                proveedor.getClientId(),
                proveedor.getApiKey()
        );

        if (productosProveedor.isEmpty()) {
            logger.warn("No products received from provider {}", proveedorId);
            return result;
        }

        logger.info("Received {} products from provider {}", productosProveedor.size(), proveedorId);

        // 2. Get assigned products
        List<Integer> codigosAsignados = productoRepository.findByProviderId(proveedorId).stream()
                .map(Producto::getCodigoBarra)
                .toList();

        // 3. Sync prices for assigned products only
        for (Producto producto : productosProveedor) {
            if (!codigosAsignados.contains(producto.getCodigoBarra())) {
                continue; // Skip products not assigned to this provider
            }

            Float precio = extractPrecio(producto);
            if (precio == null) {
                continue; // Skip products without price
            }

            try {
                Map<String, Object> syncResult = historialPrecioRepository.syncPrecio(
                        producto.getCodigoBarra(),
                        precio,
                        proveedorId
                );

                updateResultCounters(result, syncResult);

            } catch (Exception e) {
                logger.error("Error syncing product {}: {}", producto.getCodigoBarra(), e.getMessage());
                result.put("errors", result.get("errors") + 1);
            }
        }

        logger.info("Sync completed. Created: {}, Updated: {}, Errors: {}",
                result.get("pricesCreated"), result.get("pricesUpdated"), result.get("errors"));

        return result;
    }

    private Float extractPrecio(Producto producto) {
        if (producto.getPrecios() != null && !producto.getPrecios().isEmpty()) {
            return producto.getPrecios().get(0).getPrecio();
        }
        return null;
    }

    private void updateResultCounters(Map<String, Integer> result, Map<String, Object> syncResult) {
        String action = (String) syncResult.get("action");
        if ("PRICE_CREATED".equals(action)) {
            result.put("pricesCreated", result.get("pricesCreated") + 1);
        } else if ("PRICE_UPDATED".equals(action)) {
            result.put("pricesUpdated", result.get("pricesUpdated") + 1);
        }
    }

    private Proveedor getProveedor(Integer proveedorId) {
        return proveedorRepository.findById(proveedorId)
            .orElseThrow(() -> new ProveedorNotFoundException(
                "Provider with ID " + proveedorId + " not found"
            ));
    }
}
