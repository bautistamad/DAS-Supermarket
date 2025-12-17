package ubp.edu.com.ar.finalproyect.adapter.external.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.pedidos.*;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.*;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.utils.*;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.utils.AuthRequest;
import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("soapProveedorAdapter")
public class SoapProveedorAdapter implements ProveedorIntegration {

    private static final Logger logger = LoggerFactory.getLogger(SoapProveedorAdapter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final SoapClientFactory soapClientFactory;

    public SoapProveedorAdapter(SoapClientFactory soapClientFactory) {
        this.soapClientFactory = soapClientFactory;
    }

    @Override
    public boolean checkHealth(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.debug("Checking health for SOAP endpoint: {}", apiEndpoint);
            UtilsWS utilsClient = soapClientFactory.buildUtilsClient(apiEndpoint + "/ws/utils");

            AuthRequest request = new AuthRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);

            StatusResponse response =
                utilsClient.health(request);

            boolean isHealthy = "OK".equals(response.getStatus());
            logger.info("Health check result: {}", isHealthy);
            return isHealthy;
        } catch (Exception e) {
            logger.error("Error checking health for SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Producto> getProductos(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.debug("Fetching productos from SOAP endpoint: {}", apiEndpoint);
            ProductoWS productoClient = soapClientFactory.buildProductoClient(apiEndpoint + "/ws/productos");

            ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.AuthRequest request =
                new ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.AuthRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);

            List<ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.ProductoBean> soapProductos =
                productoClient.getAllProductos(request);

            List<Producto> productos = new ArrayList<>();
            for (ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.ProductoBean soapProducto : soapProductos) {
                Producto producto = new Producto();
                producto.setCodigoBarra(soapProducto.getCodigoBarra());
                producto.setNombre(soapProducto.getNombre());
                producto.setImage(soapProducto.getImagen());

                // Create a HistorialPrecio with the current price
                ubp.edu.com.ar.finalproyect.domain.HistorialPrecio precio =
                    new ubp.edu.com.ar.finalproyect.domain.HistorialPrecio();
                precio.setCodigoBarra(soapProducto.getCodigoBarra());
                precio.setPrecio(soapProducto.getPrecio());
                precio.setFechaInicio(LocalDateTime.now());
                precio.setFechaFin(null); // Current price

                // Set the price in the producto
                producto.setPrecios(List.of(precio));

                productos.add(producto);
            }

            logger.info("Fetched {} productos from SOAP service", productos.size());
            return productos;
        } catch (Exception e) {
            logger.error("Error fetching productos from SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<EscalaDefinicion> getEscala(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.debug("Fetching escala from SOAP endpoint: {}", apiEndpoint);
            UtilsWS utilsClient = soapClientFactory.buildUtilsClient(apiEndpoint + "/ws/utils");

            ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.utils.AuthRequest request =
                new ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.utils.AuthRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);

            List<PonderacionBean> soapPonderaciones = utilsClient.getPonderaciones(request);

            List<EscalaDefinicion> escalas = new ArrayList<>();
            for (PonderacionBean soapPonderacion : soapPonderaciones) {
                EscalaDefinicion escala = new EscalaDefinicion();
                escala.setValor(String.valueOf(soapPonderacion.getPuntuacion()));
                escala.setDescripcion(soapPonderacion.getDescripcion());
                escalas.add(escala);
            }

            logger.info("Fetched {} escalas from SOAP service", escalas.size());
            return escalas;
        } catch (Exception e) {
            logger.error("Error fetching escala from SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Pedido asignarPedido(String apiEndpoint, String clientId, String apiKey, Pedido pedido) {
        try {
            logger.debug("Assigning pedido via SOAP endpoint: {}", apiEndpoint);
            PedidoWS pedidoClient = soapClientFactory.buildPedidoClient(apiEndpoint + "/ws/pedidos");

            AsignarPedidoRequest request = new AsignarPedidoRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);

            ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.pedidos.Pedido soapPedido =
                new ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.pedidos.Pedido();

            for (PedidoProducto producto : pedido.getProductos()) {
                ProductoPedido soapProducto = new ProductoPedido();
                soapProducto.setCodigoBarra(producto.getCodigoBarraProveedor() != null ?
                    producto.getCodigoBarraProveedor() : producto.getCodigoBarra());
                soapProducto.setCantidad(producto.getCantidad());
                soapPedido.getProductos().add(soapProducto);
            }

            request.setPedido(soapPedido);

            AsignarPedidoResponse response = pedidoClient.asignarPedido(request);

            Pedido resultPedido = new Pedido();
            resultPedido.setIdPedidoProveedor(response.getIdPedido());
            resultPedido.setEstadoNombre(response.getEstadoPedido());
            resultPedido.setFechaEstimada(parseDateTime(response.getFechaEstimada()));

            logger.info("Pedido assigned successfully. Provider pedido ID: {}", response.getIdPedido());
            return resultPedido;
        } catch (Exception e) {
            logger.error("Error assigning pedido via SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Pedido consultarEstado(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        try {
            logger.debug("Consulting pedido status via SOAP endpoint: {}, pedidoId: {}", apiEndpoint, idPedido);
            PedidoWS pedidoClient = soapClientFactory.buildPedidoClient(apiEndpoint + "/ws/pedidos");

            ConsultarEstadoRequest request = new ConsultarEstadoRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);
            request.setIdPedido(idPedido);

            ConsultarEstadoResponse response = pedidoClient.consultarEstado(request);

            Pedido pedido = new Pedido();
            pedido.setIdPedidoProveedor(Math.toIntExact(response.getIdPedido()));
            pedido.setEstadoNombre(response.getEstado());
            pedido.setEstadoDescripcion(response.getDescripcion());

            logger.info("Pedido status consulted successfully. Estado: {}", response.getEstado());
            return pedido;
        } catch (Exception e) {
            logger.error("Error consulting pedido status via SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Pedido cancelarPedido(String apiEndpoint, String clientId, String apiKey, Integer idPedido) {
        try {
            logger.debug("Canceling pedido via SOAP endpoint: {}, pedidoId: {}", apiEndpoint, idPedido);
            PedidoWS pedidoClient = soapClientFactory.buildPedidoClient(apiEndpoint + "/ws/pedidos");

            CancelarPedidoRequest request = new CancelarPedidoRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);
            request.setIdPedido(idPedido);

            CancelarPedidoResponse response = pedidoClient.cancelarPedido(request);

            Pedido pedido = new Pedido();
            pedido.setIdPedidoProveedor(Math.toIntExact(response.getIdPedido()));
            pedido.setEstadoNombre(response.getEstado());
            pedido.setEstadoDescripcion(response.getDescripcion());

            logger.info("Pedido canceled successfully. Estado: {}", response.getEstado());
            return pedido;
        } catch (Exception e) {
            logger.error("Error canceling pedido via SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean enviarEvaluacion(String apiEndpoint, String clientId, String apiKey,
                                   Integer pedidoId, Integer puntuacion) {
        try {
            logger.debug("Sending evaluation via SOAP endpoint: {}, pedidoId: {}, rating: {}",
                apiEndpoint, pedidoId, puntuacion);
            PedidoWS pedidoClient = soapClientFactory.buildPedidoClient(apiEndpoint + "/ws/pedidos");

            PuntuarPedidoRequest request = new PuntuarPedidoRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);
            request.setIdPedido(pedidoId);
            request.setPuntuacion(puntuacion);

            PuntuarPedidoResponse response = pedidoClient.puntuarPedido(request);

            logger.info("Evaluation sent successfully for pedido {}: {}", pedidoId, response.getDescripcion());
            return true;
        } catch (Exception e) {
            logger.error("Error sending evaluation via SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> estimarPedido(String apiEndpoint, String clientId, String apiKey) {
        try {
            logger.debug("Estimating pedido via SOAP endpoint: {}", apiEndpoint);
            PedidoWS pedidoClient = soapClientFactory.buildPedidoClient(apiEndpoint + "/ws/pedidos");

            EstimarPedidoRequest request = new EstimarPedidoRequest();
            request.setClientId(clientId);
            request.setApikey(apiKey);

            EstimarPedidoResponse response = pedidoClient.estimarPedido(request);

            Map<String, Object> estimacion = new HashMap<>();
            estimacion.put("fechaEstimada", response.getFechaEstimada());

            logger.info("Pedido estimated successfully. Fecha estimada: {}", response.getFechaEstimada());
            return estimacion;
        } catch (Exception e) {
            logger.error("Error estimating pedido via SOAP endpoint {}: {}", apiEndpoint, e.getMessage(), e);
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
        } catch (Exception e) {
            logger.warn("Could not parse datetime '{}': {}", dateTimeStr, e.getMessage());
            return null;
        }
    }

}
