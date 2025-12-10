package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.domain.ProductoProveedor;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.ProveedorNotFoundException;
import ubp.edu.com.ar.finalproyect.port.HistorialPrecioRepository;
import ubp.edu.com.ar.finalproyect.port.ProductoProveedorRepository;
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
    private final ProductoProveedorRepository productoProveedorRepository;
    private final HistorialPrecioRepository historialPrecioRepository;
    private final ProveedorIntegrationFactory factory;

    public ProveedorIntegrationService(ProveedorRepository proveedorRepository,
                                       ProductoRepository productoRepository,
                                       ProductoProveedorRepository productoProveedorRepository,
                                       HistorialPrecioRepository historialPrecioRepository,
                                       ProveedorIntegrationFactory factory) {
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.productoProveedorRepository = productoProveedorRepository;
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

    /**
     * Query order status from provider
     * Returns current status of the order at the provider
     */
    public Pedido consultarEstadoPedido(Integer proveedorId, Integer pedidoId) {
        logger.info("Querying status for order {} from provider ID: {}", pedidoId, proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        try {
            ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
            Pedido pedidoEstado = adapter.consultarEstado(
                proveedor.getApiEndpoint(),
                proveedor.getClientId(),
                proveedor.getApiKey(),
                pedidoId
            );

            if (pedidoEstado != null) {
                logger.info("Successfully queried status for order {} from provider {}: {}",
                    pedidoId, proveedorId, pedidoEstado.getEstadoNombre());
            } else {
                logger.warn("Failed to query status for order {} from provider {}. Provider returned null.",
                    pedidoId, proveedorId);
            }

            return pedidoEstado;

        } catch (Exception e) {
            logger.error("Exception occurred while querying status for order {} from provider {}",
                pedidoId, proveedorId, e);
            return null;
        }
    }

    /**
     * Assign order to provider (create order in provider's system)
     * @param proveedorId Provider ID
     * @param pedido Order to assign with products
     * @return Updated Pedido with provider confirmation or null if failed
     */
    public Pedido asignarPedidoWithProveedor(Integer proveedorId, Pedido pedido) {
        logger.info("Attempting to assign order to provider ID: {}", proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        try {
            ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
            Pedido pedidoAsignado = adapter.asignarPedido(
                proveedor.getApiEndpoint(),
                proveedor.getClientId(),
                proveedor.getApiKey(),
                pedido
            );

            if (pedidoAsignado != null) {
                logger.info("Successfully assigned order to provider {}. Order ID: {}",
                    proveedorId, pedido.getId());
            } else {
                logger.warn("Failed to assign order to provider {}. Provider did not confirm assignment.",
                    proveedorId);
            }

            return pedidoAsignado;

        } catch (Exception e) {
            logger.error("Exception occurred while assigning order to provider {}",
                proveedorId, e);
            return null;
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

        // 2. Build mapping: codigoBarraProveedor → codigoBarra (internal)
        //    Provider returns products with THEIR barcode, we need to map to OUR internal barcode
        List<Producto> productosAsignados = productoRepository.findByProviderId(proveedorId);
        Map<Integer, Integer> proveedorToInternalMap = new HashMap<>();

        for (Producto p : productosAsignados) {
            // Get the ProductoProveedor mapping to find codigoBarraProveedor
            ProductoProveedor mapping = productoProveedorRepository.findByProveedorAndProducto(
                    proveedorId,
                    p.getCodigoBarra()
            );

            if (mapping != null && mapping.getCodigoBarraProveedor() != null) {
                // Map: provider's barcode → our internal barcode
                proveedorToInternalMap.put(mapping.getCodigoBarraProveedor(), p.getCodigoBarra());
                logger.debug("Mapped provider barcode {} to internal barcode {}",
                        mapping.getCodigoBarraProveedor(), p.getCodigoBarra());
            }
        }

        logger.info("Mapped {} products for provider {}", proveedorToInternalMap.size(), proveedorId);

        // 3. Sync prices for assigned products only
        for (Producto productoProveedor : productosProveedor) {
            // productoProveedor.getCodigoBarra() contains the PROVIDER's barcode
            Integer codigoBarraInterno = proveedorToInternalMap.get(productoProveedor.getCodigoBarra());

            if (codigoBarraInterno == null) {
                logger.debug("Skipping provider product {} - not mapped to internal product",
                        productoProveedor.getCodigoBarra());
                continue; // Skip products not assigned to this provider
            }

            Float precio = extractPrecio(productoProveedor);
            if (precio == null) {
                logger.warn("Skipping product {} - no price available", productoProveedor.getCodigoBarra());
                continue; // Skip products without price
            }

            try {
                Map<String, Object> syncResult = historialPrecioRepository.syncPrecio(
                        codigoBarraInterno,  // Use internal barcode for our database
                        precio,
                        proveedorId
                );

                updateResultCounters(result, syncResult);

            } catch (Exception e) {
                logger.error("Error syncing product {}: {}", codigoBarraInterno, e.getMessage());
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

    /**
     * Get available products from provider
     * Returns list of products available at the provider for selection
     */
    public List<Producto> getProductosDisponiblesFromProveedor(Integer proveedorId) {
        logger.info("Fetching available products from provider ID: {}", proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
        List<Producto> productos = adapter.getProductos(
                proveedor.getApiEndpoint(),
                proveedor.getClientId(),
                proveedor.getApiKey()
        );

        logger.info("Fetched {} products from provider {}", productos.size(), proveedorId);
        return productos;
    }

    /**
     * Fetch rating scale from provider's API
     * Returns list of scale values (e.g., "Excelente", "Bueno", "Regular")
     */
    public List<ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion> fetchEscalaFromProveedor(Integer proveedorId) {
        logger.info("Fetching rating scale for provider ID: {}", proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        try {
            ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
            List<ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion> escalas = adapter.getEscala(
                    proveedor.getApiEndpoint(),
                    proveedor.getClientId(),
                    proveedor.getApiKey()
            );

            if (escalas != null && !escalas.isEmpty()) {
                logger.info("Successfully fetched {} scale values from provider {}", escalas.size(), proveedorId);
            } else {
                logger.warn("Provider {} returned empty or null scale", proveedorId);
            }

            return escalas;

        } catch (Exception e) {
            logger.error("Failed to fetch scale from provider {}", proveedorId, e);
            return List.of();
        }
    }

    /**
     * Send order evaluation to provider
     * @param proveedorId Provider ID
     * @param pedidoId Order ID
     * @param puntuacion Rating value in provider's scale
     * @return true if evaluation was sent successfully, false otherwise
     */
    public boolean enviarEvaluacionToProveedor(Integer proveedorId, Integer pedidoId, Integer puntuacion) {
        logger.info("Sending evaluation for order {} to provider ID: {} with rating: {}",
                pedidoId, proveedorId, puntuacion);

        Proveedor proveedor = getProveedor(proveedorId);

        try {
            ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
            boolean success = adapter.enviarEvaluacion(
                    proveedor.getApiEndpoint(),
                    proveedor.getClientId(),
                    proveedor.getApiKey(),
                    pedidoId,
                    puntuacion
            );

            if (success) {
                logger.info("Successfully sent evaluation for order {} to provider {}", pedidoId, proveedorId);
            } else {
                logger.warn("Failed to send evaluation for order {} to provider {}", pedidoId, proveedorId);
            }

            return success;

        } catch (Exception e) {
            logger.error("Exception occurred while sending evaluation for order {} to provider {}",
                    pedidoId, proveedorId, e);
            return false;
        }
    }

    /**
     * Estimate order price from provider
     * @param proveedorId Provider ID
     * @param pedido Order with products to estimate
     * @return Map with estimation details (precioEstimadoTotal, fechaEstimada, productosJson) or null if failed
     */
    public Map<String, Object> estimarPedidoWithProveedor(Integer proveedorId, Pedido pedido) {
        logger.info("Estimating order with provider ID: {}", proveedorId);

        Proveedor proveedor = getProveedor(proveedorId);

        try {
            ProveedorIntegration adapter = factory.getAdapter(proveedor.getTipoServicio());
            Map<String, Object> estimacion = adapter.estimarPedido(
                    proveedor.getApiEndpoint(),
                    proveedor.getClientId(),
                    proveedor.getApiKey(),
                    pedido
            );

            if (estimacion != null) {
                logger.info("Successfully estimated order with provider {}. Total price: {}",
                        proveedorId, estimacion.get("precioEstimadoTotal"));
            } else {
                logger.warn("Failed to estimate order with provider {}. Provider did not return estimation.",
                        proveedorId);
            }

            return estimacion;

        } catch (Exception e) {
            logger.error("Exception occurred while estimating order with provider {}",
                    proveedorId, e);
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
