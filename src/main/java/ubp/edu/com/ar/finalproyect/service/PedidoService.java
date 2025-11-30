package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.PedidoNotFoundException;
import ubp.edu.com.ar.finalproyect.port.PedidoRepository;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository repository;
    private final ProveedorRepository proveedorRepository;
    private final ProveedorIntegrationService integrationService;
    private final EscalaService escalaService;

    public PedidoService(PedidoRepository repository, ProveedorRepository proveedorRepository,
                         ProveedorIntegrationService integrationService,
                         EscalaService escalaService) {
        this.repository = repository;
        this.proveedorRepository = proveedorRepository;
        this.integrationService = integrationService;
        this.escalaService = escalaService;
    }

    @Transactional
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
        if (pedido.getProductos() == null || pedido.getProductos().isEmpty()) {
            throw new IllegalArgumentException("Pedido must have at least one product");
        }

        logger.info("Creating order with provider {}", pedido.getProveedorId());


        // Step 2: Save order in our database (estado: Pendiente = 1)
        logger.info("Step 2: Saving order in local database");
        Pedido pedidoGuardado = repository.save(pedido);

        if (pedidoGuardado.getId() == null) {
            logger.error("Failed to save order in database");
            throw new IllegalStateException("Failed to save order in database");
        }

        logger.info("Order saved locally with ID: {}", pedidoGuardado.getId());

        // Step 3: Assign order with provider (confirm order)
        logger.info("Step 3: Assigning order {} with provider {}",
                pedidoGuardado.getId(), pedido.getProveedorId());

        logger.info("Order {} successfully assigned with provider {}",
                pedidoGuardado.getId(), pedido.getProveedorId());

        // Step 4: Update order with confirmation from provider (estado: Confirmado = 2)
        logger.info("Step 4: Updating order {} status to Confirmado", pedidoGuardado.getId());
        pedidoGuardado.setEstadoId(2); // Confirmado

        Pedido pedidoFinal = repository.update(pedidoGuardado);

        logger.info("Order {} created and confirmed successfully with provider {}",
                pedidoFinal.getId(), pedido.getProveedorId());

        return pedidoFinal;
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

    /**
     * Query order status from provider and update local database
     * Useful for syncing order status (En Preparación, En Tránsito, Entregado, etc.)
     */
    @Transactional
    public Pedido consultarEstadoPedido(Integer pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }

        logger.info("Querying status for order {}", pedidoId);

        // Get the existing order
        Pedido pedido = repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        // Query status from provider
        Pedido estadoProveedor = integrationService.consultarEstadoPedido(
                pedido.getProveedorId(),

                pedidoId
        );

        if (estadoProveedor == null) {
            logger.warn("Provider did not return status for order {}", pedidoId);
            throw new IllegalStateException(
                    "Failed to query order status from provider. Please try again later."
            );
        }

        logger.info("Provider returned status for order {}: {}",
                pedidoId, estadoProveedor.getEstadoNombre());

        // Map provider status to our internal estado ID
        Integer nuevoEstadoId = mapProviderStatusToEstadoId(estadoProveedor.getEstadoNombre());

        if (nuevoEstadoId != null && !nuevoEstadoId.equals(pedido.getEstadoId())) {
            logger.info("Updating order {} status from {} to {}",
                    pedidoId, pedido.getEstadoId(), nuevoEstadoId);

            pedido.setEstadoId(nuevoEstadoId);
            pedido.setEstadoNombre(estadoProveedor.getEstadoNombre());
            pedido.setEstadoDescripcion(estadoProveedor.getEstadoDescripcion());

            // Update in database
            Pedido updated = repository.update(pedido);

            logger.info("Successfully updated order {} status to {}",
                    pedidoId, estadoProveedor.getEstadoNombre());

            return updated;
        } else {
            logger.info("Order {} status unchanged: {}", pedidoId, pedido.getEstadoNombre());
            return pedido;
        }
    }

    /**
     * Map provider's status string to our internal EstadoPedido ID
     * Provider statuses: "Asignado", "En Proceso", "En camino", "Entregado", "Cancelado"
     * Our statuses: 1=Pendiente, 2=Confirmado, 3=En Preparación, 4=En Tránsito, 5=Entregado, 6=Cancelado
     */
    private Integer mapProviderStatusToEstadoId(String providerStatus) {
        if (providerStatus == null) {
            return null;
        }

        return switch (providerStatus.toLowerCase()) {
            case "asignado" -> 2;           // Confirmado
            case "en proceso" -> 3;         // En Preparación
            case "en camino" -> 4;          // En Tránsito
            case "entregado" -> 5;          // Entregado
            case "cancelado" -> 6;          // Cancelado
            default -> {
                logger.warn("Unknown provider status: {}", providerStatus);
                yield null;
            }
        };
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
