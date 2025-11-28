package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.exception.PedidoNotFoundException;
import ubp.edu.com.ar.finalproyect.port.PedidoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository repository;
    private final ProveedorIntegrationService integrationService;

    public PedidoService(PedidoRepository repository, ProveedorIntegrationService integrationService) {
        this.repository = repository;
        this.integrationService = integrationService;
    }

    public Pedido createPedido(Pedido pedido) {
        // Validate input
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido cannot be null");
        }
        if (pedido.getEstadoId() == null) {
            throw new IllegalArgumentException("Pedido estado cannot be null");
        }
        if (pedido.getProveedorId() == null) {
            throw new IllegalArgumentException("Pedido proveedor cannot be null");
        }

        return repository.save(pedido);
    }

    public Pedido updatePedido(Pedido pedido) {
        // Validate input
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido cannot be null");
        }
        if (pedido.getId() == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        if (pedido.getEstadoId() == null) {
            throw new IllegalArgumentException("Pedido estado cannot be null");
        }

        // Check if pedido exists
        repository.findById(pedido.getId())
                .orElseThrow(() -> new PedidoNotFoundException(pedido.getId()));

        return repository.update(pedido);
    }

    public Pedido getPedido(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
    }

    public List<Pedido> getAllPedidos() {
        return repository.findAll();
    }

    public void deletePedido(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        repository.deleteById(id);
    }

    public List<Pedido> getPedidosByProveedor(Integer providerId) {

        List<Pedido> pedidos = new ArrayList<>();

        if (providerId == null) {
            throw new IllegalArgumentException("Proveedor id cannot be null");
        }

        pedidos = repository.findByProviderId(providerId);

        for (Pedido pedido : pedidos) {
            List<PedidoProducto> pedidosProducto = getProductosByPedido(pedido.getId());
            pedido.setProductos(pedidosProducto);
        }

        return pedidos;
    }

    public List<PedidoProducto> getProductosByPedido(Integer pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        // Validate pedido exists
        repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
        return repository.findProductsByPedidoId(pedidoId);
    }

    public Pedido cancelarPedido(Integer pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }

        logger.info("Attempting to cancel order {}", pedidoId);

        // Get the existing order
        Pedido pedido = repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        // Validate order can be cancelled (not already delivered, cancelled, etc)
        if (pedido.getEstadoId() == 5) { // Entregado
            throw new IllegalStateException("Cannot cancel order " + pedidoId + " - already delivered");
        }
        if (pedido.getEstadoId() == 6) { // Cancelado
            throw new IllegalStateException("Order " + pedidoId + " is already cancelled");
        }

        // Try to cancel with the provider
        Pedido pedidoCancelado = integrationService.cancelarPedidoWithProveedor(
                pedido.getProveedorId(),
                pedidoId
        );

        if (pedidoCancelado == null) {
            logger.warn("Provider did not confirm cancellation for order {}", pedidoId);
            throw new IllegalStateException(
                "Failed to cancel order with provider. Order may have already been sent or cannot be cancelled."
            );
        }

        // Update the order status in our database
        pedido.setEstadoId(6); // Cancelado
        Pedido updated = repository.update(pedido);

        logger.info("Successfully cancelled order {} and updated local status", pedidoId);
        return updated;
    }
}
