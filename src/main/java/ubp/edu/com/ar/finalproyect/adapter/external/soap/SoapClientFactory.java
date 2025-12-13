package ubp.edu.com.ar.finalproyect.adapter.external.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.pedidos.PedidoWS;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.pedidos.PedidoWSService;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.ProductoWS;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.productos.ProductoWSService;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.utils.UtilsWS;
import ubp.edu.com.ar.finalproyect.adapter.external.soap.dto.utils.UtilsWSService;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class SoapClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(SoapClientFactory.class);

    /**
     * Crea un cliente SOAP para el servicio de Pedidos
     * @param endpointUrl URL del servicio SOAP (ej: http://localhost:8086/ws/pedidos)
     * @return Cliente PedidoWS configurado
     */
    public PedidoWS buildPedidoClient(String endpointUrl) {
        try {
            URL wsdlUrl = new URL(endpointUrl + "?wsdl");
            PedidoWSService service = new PedidoWSService(wsdlUrl);
            logger.debug("Cliente SOAP Pedidos creado exitosamente para: {}", endpointUrl);
            return service.getPedidoWSPort();
        } catch (MalformedURLException e) {
            logger.error("URL de WSDL inválida: {}", endpointUrl, e);
            throw new IllegalArgumentException("La URL del servicio SOAP Pedidos es inválida: " + endpointUrl, e);
        } catch (Exception e) {
            logger.error("Error al crear el cliente SOAP Pedidos para {}: {}", endpointUrl, e.getMessage());
            throw new RuntimeException("No se pudo inicializar el cliente SOAP Pedidos", e);
        }
    }

    /**
     * Crea un cliente SOAP para el servicio de Productos
     * @param endpointUrl URL del servicio SOAP (ej: http://localhost:8086/ws/productos)
     * @return Cliente ProductoWS configurado
     */
    public ProductoWS buildProductoClient(String endpointUrl) {
        try {
            URL wsdlUrl = new URL(endpointUrl + "?wsdl");
            ProductoWSService service = new ProductoWSService(wsdlUrl);
            logger.debug("Cliente SOAP Productos creado exitosamente para: {}", endpointUrl);
            return service.getProductoWSPort();
        } catch (MalformedURLException e) {
            logger.error("URL de WSDL inválida: {}", endpointUrl, e);
            throw new IllegalArgumentException("La URL del servicio SOAP Productos es inválida: " + endpointUrl, e);
        } catch (Exception e) {
            logger.error("Error al crear el cliente SOAP Productos para {}: {}", endpointUrl, e.getMessage());
            throw new RuntimeException("No se pudo inicializar el cliente SOAP Productos", e);
        }
    }

    /**
     * Crea un cliente SOAP para el servicio de Utils
     * @param endpointUrl URL del servicio SOAP (ej: http://localhost:8086/ws/utils)
     * @return Cliente UtilsWS configurado
     */
    public UtilsWS buildUtilsClient(String endpointUrl) {
        try {
            URL wsdlUrl = new URL(endpointUrl + "?wsdl");
            UtilsWSService service = new UtilsWSService(wsdlUrl);
            logger.debug("Cliente SOAP Utils creado exitosamente para: {}", endpointUrl);
            return service.getUtilsWSPort();
        } catch (MalformedURLException e) {
            logger.error("URL de WSDL inválida: {}", endpointUrl, e);
            throw new IllegalArgumentException("La URL del servicio SOAP Utils es inválida: " + endpointUrl, e);
        } catch (Exception e) {
            logger.error("Error al crear el cliente SOAP Utils para {}: {}", endpointUrl, e.getMessage());
            throw new RuntimeException("No se pudo inicializar el cliente SOAP Utils", e);
        }
    }
}
