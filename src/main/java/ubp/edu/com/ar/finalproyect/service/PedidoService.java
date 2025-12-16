package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Escala;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.EscalaNotFoundException;
import ubp.edu.com.ar.finalproyect.exception.PedidoNotFoundException;
import ubp.edu.com.ar.finalproyect.port.EscalaRepository;
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
    private final EscalaRepository escalaRepository;

    public PedidoService(PedidoRepository repository, ProveedorRepository proveedorRepository,
                         ProveedorIntegrationService integrationService,
                         EscalaService escalaService,
                         EscalaRepository escalaRepository) {
        this.repository = repository;
        this.proveedorRepository = proveedorRepository;
        this.integrationService = integrationService;
        this.escalaService = escalaService;
        this.escalaRepository = escalaRepository;
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

        // Step 1: Save order in our database (estado: Pendiente = 1)
        logger.info("Step 1: Saving order in local database");
        Pedido pedidoGuardado = repository.save(pedido);

        if (pedidoGuardado.getId() == null) {
            logger.error("Failed to save order in database");
            throw new IllegalStateException("Failed to save order in database");
        }

        logger.info("Order saved locally with ID: {}", pedidoGuardado.getId());

        // Step 2: Assign order with provider (confirm order with external system)
        logger.info("Step 2: Assigning order {} with provider {}",
                pedidoGuardado.getId(), pedido.getProveedorId());

        Pedido pedidoAsignado = integrationService.asignarPedidoWithProveedor(
                pedido.getProveedorId(),
                pedidoGuardado
        );

        if (pedidoAsignado == null) {
            logger.error("Failed to assign order {} with provider {}. Rolling back to Pendiente state.",
                    pedidoGuardado.getId(), pedido.getProveedorId());
            // Keep order in Pendiente state - can retry assignment later with POST /pedidos/{id}/asignar
            return pedidoGuardado;
        }

        logger.info("Order {} successfully assigned with provider {}",
                pedidoGuardado.getId(), pedido.getProveedorId());

        // Step 3: Update order with confirmation from provider (estado: En Proceso = 2)
        logger.info("Step 3: Updating order {} status to En Proceso", pedidoGuardado.getId());
        pedidoAsignado.setId(pedidoGuardado.getId()); // Preserve our internal ID
        pedidoAsignado.setProveedorId(pedido.getProveedorId()); // Preserve proveedor
        pedidoAsignado.setProductos(pedidoGuardado.getProductos()); // Preserve products
        pedidoAsignado.setEstadoId(2); // Set to "En Proceso" status

        logger.info("About to update order {} with: estado={}, idPedidoProveedor={}, fechaEstimada={}",
                pedidoAsignado.getId(), pedidoAsignado.getEstadoId(),
                pedidoAsignado.getIdPedidoProveedor(), pedidoAsignado.getFechaEstimada());

        Pedido pedidoFinal = repository.update(pedidoAsignado);

        logger.info("Order {} updated successfully. idPedidoProveedor saved: {}",
                pedidoFinal.getId(), pedidoFinal.getIdPedidoProveedor());

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


    @Transactional
    public Pedido consultarEstadoPedido(Integer pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }

        logger.info("Querying status for order {}", pedidoId);

        Pedido pedido = repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

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
     * Our statuses: 1=Pendiente, 2=En Proceso, 3=Enviado, 4=Entregado, 5=Cancelado
     */
    private Integer mapProviderStatusToEstadoId(String providerStatus) {
        if (providerStatus == null) {
            return null;
        }

        return switch (providerStatus.toLowerCase()) {
            case "asignado" -> 2;           // En Proceso
            case "en proceso" -> 2;         // En Proceso
            case "en camino" -> 3;          // Enviado
            case "entregado" -> 4;          // Entregado
            case "cancelado" -> 5;          // Cancelado
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
        if (pedido.getEstadoId() == 4) { // Entregado
            throw new IllegalStateException("Cannot cancel order " + pedidoId + " - already delivered");
        }
        if (pedido.getEstadoId() == 5) { // Cancelado
            throw new IllegalStateException("Order " + pedidoId + " is already cancelled");
        }

        // Try to cancel with the provider
        Pedido pedidoCancelado = integrationService.cancelarPedidoWithProveedor(
                pedido.getProveedorId(),
                pedido.getIdPedidoProveedor()
        );

        if (pedidoCancelado == null) {
            logger.warn("Provider did not confirm cancellation for order {}", pedidoId);
            throw new IllegalStateException(
                    "Failed to cancel order with provider. Order may have already been sent or cannot be cancelled."
            );
        }

        // Update the order status in our database
        pedido.setEstadoId(5); // Cancelado
        Pedido updated = repository.update(pedido);

        logger.info("Successfully cancelled order {} and updated local status", pedidoId);
        return updated;
    }
    
    @Transactional
    public Pedido ratePedido(Integer pedidoId, Integer rating) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5, got: " + rating);
        }

        logger.info("Rating order {} with internal scale: {}", pedidoId, rating);

        // Get the existing order
        Pedido pedido = repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        // Validate order is delivered (estado = 4)
        if (pedido.getEstadoId() != 4) {
            throw new IllegalStateException(
                    "Cannot rate order " + pedidoId + " - order must be delivered. Current status: " +
                    pedido.getEstadoNombre()
            );
        }

        // Find the escala mapping for this provider and internal rating
        Escala escala = escalaRepository.findByInternal(pedido.getProveedorId(), rating)
                .orElseThrow(() -> new EscalaNotFoundException(
                        "No scale mapping found for provider " + pedido.getProveedorId() +
                        " and internal rating " + rating + ". Please configure scale mappings first."
                ));

        logger.info("Converting internal rating {} to external scale {} (idEscala: {})",
                rating, escala.getEscalaExt(), escala.getIdEscala());

        // Update pedido evaluation with the scale mapping
        escalaRepository.updatePedidoEvaluacion(pedidoId, escala.getIdEscala());

        // Send evaluation to provider with external scale value
        // IMPORTANT: Use provider's order ID, not our internal ID
        if (pedido.getIdPedidoProveedor() != null) {
            Integer externalRating = Integer.parseInt(escala.getEscalaExt());
            boolean evaluacionEnviada = integrationService.enviarEvaluacionToProveedor(
                    pedido.getProveedorId(),
                    pedido.getIdPedidoProveedor(),  // Use provider's order ID
                    externalRating
            );

            if (evaluacionEnviada) {
                logger.info("Evaluation successfully sent to provider for order {} (provider order ID: {})",
                        pedidoId, pedido.getIdPedidoProveedor());
            } else {
                logger.warn("Failed to send evaluation to provider for order {} (provider order ID: {}). Evaluation saved locally.",
                        pedidoId, pedido.getIdPedidoProveedor());
            }
        } else {
            logger.warn("Cannot send evaluation to provider: idPedidoProveedor is null for order {}. Evaluation saved locally only.", pedidoId);
        }

        // Fetch and return the updated order
        Pedido updated = repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        logger.info("Successfully rated order {} with scale {}", pedidoId, escala.getDescripcionExt());
        return updated;
    }
}
