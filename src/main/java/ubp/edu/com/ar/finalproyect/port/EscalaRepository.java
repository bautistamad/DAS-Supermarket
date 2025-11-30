package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Escala;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Escala (Rating Scale Mappings)
 * Simplified to only essential operations
 */
public interface EscalaRepository {

    /**
     * Save or update a scale mapping
     * Allows saving unmapped scales (escalaInt = null) from provider
     */
    Escala save(Escala escala);

    /**
     * Save multiple scale mappings
     */
    List<Escala> saveAll(List<Escala> escalas);

    /**
     * Find all scale mappings for a provider
     * Used by frontend to display and map scales
     */
    List<Escala> findByProveedor(Integer idProveedor);

    /**
     * Find external scale value from internal value (1-5)
     * Used when converting from our internal scale to provider's scale
     * Example: Convert 5 -> "Excelente" to send rating to provider
     */
    Optional<Escala> findByInternal(Integer idProveedor, Integer escalaInt);

    /**
     * Update pedido evaluation with scale mapping
     * Only allows rating delivered orders (estado = 5)
     */
    void updatePedidoEvaluacion(Integer idPedido, Integer idEscala);
}
