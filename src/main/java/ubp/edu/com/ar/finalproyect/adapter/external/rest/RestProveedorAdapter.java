package ubp.edu.com.ar.finalproyect.adapter.external.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.AsignarPedidoRequest;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.AsignarPedidoResponse;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.CancelacionPedido;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.HealthResponse;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.PonderacionDTO;
import ubp.edu.com.ar.finalproyect.adapter.external.rest.dto.ProductoProveedorDTO;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;
import ubp.edu.com.ar.finalproyect.utils.Httpful;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("restProveedorAdapter")
public class RestProveedorAdapter implements ProveedorIntegration {

    private static final Logger logger = LoggerFactory.getLogger(RestProveedorAdapter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RestProveedorAdapter() {
    }

    @Override
    public boolean checkHealth(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.info("Checking health for provider: {} with clientId: {}", apiEndpoint, clientId);

            HealthResponse response = new Httpful(apiEndpoint)
                    .path("/api/health")
                    .addQueryParam("clientId", clientId)
                    .addQueryParam("apikey", apiKey)
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

    @Override
    public List<Producto> getProductos(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.info("Fetching products from provider: {} (clientId: {})", apiEndpoint, clientId);

            // Provider returns array directly: [{...}, {...}]
            ProductoProveedorDTO[] productosArray = new Httpful(apiEndpoint)
                    .path("/api/productos")
                    .addQueryParam("clientId", clientId)
                    .addQueryParam("apikey", apiKey)
                    .get()
                    .execute(ProductoProveedorDTO[].class);

            if (productosArray == null || productosArray.length == 0) {
                logger.warn("Received null or empty product array from provider: {}", apiEndpoint);
                return new ArrayList<>();
            }

            logger.info("Successfully fetched {} products from provider", productosArray.length);

            // Convert DTO array to Domain list
            List<Producto> productos = new ArrayList<>();
            for (ProductoProveedorDTO dto : productosArray) {
                productos.add(convertToDomain(dto));
            }
            return productos;

        } catch (Exception e) {
            logger.error("Failed to fetch products from provider endpoint: {}", apiEndpoint, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<EscalaDefinicion> getEscala(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.info("Fetching rating scale from provider: {} (clientId: {})", apiEndpoint, clientId);

            // Provider uses /api/ponderaciones endpoint
            PonderacionDTO[] ponderaciones = new Httpful(apiEndpoint)
                    .path("/api/ponderaciones")
                    .addQueryParam("clientId", clientId)
                    .addQueryParam("apikey", apiKey)
                    .get()
                    .execute(PonderacionDTO[].class);

            if (ponderaciones == null || ponderaciones.length == 0) {
                logger.warn("Received null or empty ponderacion array from provider: {}", apiEndpoint);
                return new ArrayList<>();
            }

            logger.info("Successfully fetched {} scale values from provider", ponderaciones.length);

            // Convert PonderacionDTO to EscalaDefinicion
            List<EscalaDefinicion> escalas = new ArrayList<>();
            for (PonderacionDTO ponderacion : ponderaciones) {
                EscalaDefinicion escala = new EscalaDefinicion();
                // Convert puntuacion (int) to valor (String)
                escala.setValor(String.valueOf(ponderacion.getPuntuacion()));
                escala.setDescripcion(ponderacion.getDescripcion());
                escalas.add(escala);
            }

            return escalas;

        } catch (Exception e) {
            logger.error("Failed to fetch scale from provider endpoint: {}", apiEndpoint, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Pedido asignarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido) {
        try {
            logger.info("Assigning order to provider: {} (clientId: {})", apiEndpoint, clientId);

            // Build request with productos from pedido using Maps
            List<Map<String, Integer>> productosRequest = new ArrayList<>();
            for (PedidoProducto pp : pedido.getProductos()) {
                Map<String, Integer> producto = new HashMap<>();
                producto.put("codigoBarra", pp.getCodigoBarra());
                producto.put("cantidad", pp.getCantidad());
                productosRequest.add(producto);
            }

            AsignarPedidoRequest request = new AsignarPedidoRequest(productosRequest);

            // Send request to provider
            AsignarPedidoResponse response = new Httpful(apiEndpoint)
                    .path("/api/proveedor/asignarPedido")
                    .addQueryParam("clientId", clientId)
                    .addQueryParam("apikey", apiKey)
                    .post(request)
                    .execute(AsignarPedidoResponse.class);

            if (response == null || response.getPedido() == null) {
                logger.error("Received null response from provider for order assignment");
                return null;
            }

            Map<String, Object> providerPedido = response.getPedido();
            Object idPedidoObj = providerPedido.get("idPedido");
            logger.info("Order successfully assigned with provider. Provider order ID: {}", idPedidoObj);

            // Update our pedido with provider's response
            Pedido updatedPedido = new Pedido();
            updatedPedido.setId(pedido.getId()); // Keep our internal ID
            updatedPedido.setProveedorId(pedido.getProveedorId());
            updatedPedido.setEstadoId(2); // Confirmado (estado 2)
            updatedPedido.setEstadoNombre("Confirmado");

            // Parse fechaEstimada from provider
            String fechaEstimada = (String) providerPedido.get("fechaEstimada");
            if (fechaEstimada != null) {
                try {
                    updatedPedido.setFechaEstimada(
                        LocalDateTime.parse(fechaEstimada, DATE_FORMATTER)
                    );
                } catch (Exception e) {
                    logger.warn("Failed to parse fechaEstimada: {}", fechaEstimada);
                }
            }

            updatedPedido.setProductos(pedido.getProductos());

            return updatedPedido;

        } catch (Exception e) {
            logger.error("Failed to assign order with provider endpoint: {}", apiEndpoint, e);
            return null;
        }
    }

    @Override
    public Pedido consultarEstado(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        return null;
    }

    private Producto convertToDomain(ProductoProveedorDTO dto) {
        Producto producto = new Producto();
        producto.setCodigoBarra(dto.getCodigoBarra());
        producto.setNombre(dto.getNombre());

        // Create a HistorialPrecio with the current price
        HistorialPrecio precio = new HistorialPrecio();
        precio.setCodigoBarra(dto.getCodigoBarra());
        precio.setPrecio(dto.getPrecio());
        precio.setFechaInicio(LocalDateTime.now());
        precio.setFechaFin(null); // Current price

        // Set the price in the producto
        producto.setPrecios(List.of(precio));

        return producto;
    }

    @Override
    public Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        try {
            logger.info("Attempting to cancel order {} with provider: {} (clientId: {})", idPedido, apiEndpoint,
                    clientId);

            CancelacionPedido response = new Httpful(apiEndpoint)
                    .path("/api/cancelarPedido")
                    .addQueryParam("clientId", clientId)
                    .addQueryParam("apikey", apiKey)
                    .addQueryParam("idPedido", idPedido.toString())
                    .get()
                    .execute(CancelacionPedido.class);

            if (response == null) {
                logger.error("Received null response from provider for order cancellation: {}", idPedido);
                return null;
            }

            logger.info("Cancellation response for order {}: status={}, description={}",
                    idPedido, response.getEstado(), response.getDescription());

            // Check if the cancellation was successful
            if (!response.isCancelled()) {
                logger.warn("Order {} could not be cancelled. Status: {}, Description: {}",
                        idPedido, response.getEstado(), response.getDescription());
                return null;
            }

            // Create a Pedido object with updated status
            Pedido pedido = new Pedido();
            pedido.setId(idPedido);
            pedido.setEstadoId(6); // EstadoPedido: Cancelado
            pedido.setEstadoNombre("Cancelado");
            pedido.setEstadoDescripcion(response.getDescription());

            logger.info("Successfully cancelled order {} with provider", idPedido);
            return pedido;

        } catch (Exception e) {
            logger.error("Failed to cancel order {} with provider endpoint: {}", idPedido, apiEndpoint, e);
            return null;
        }
    }

}