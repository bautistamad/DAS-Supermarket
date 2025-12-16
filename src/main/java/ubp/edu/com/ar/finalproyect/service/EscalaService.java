package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Escala;
import ubp.edu.com.ar.finalproyect.exception.EscalaNotFoundException;
import ubp.edu.com.ar.finalproyect.port.EscalaRepository;

import java.util.List;


@Service
public class EscalaService {

    private static final Logger logger = LoggerFactory.getLogger(EscalaService.class);

    @Autowired
    private EscalaRepository escalaRepository;

    @Transactional(readOnly = true)
    public List<Escala> getMappingsByProveedor(Integer proveedorId) {
        logger.info("Getting scale mappings for provider: {}", proveedorId);
        List<Escala> escalas = escalaRepository.findByProveedor(proveedorId);
        logger.info("Found {} scale mappings for provider {}", escalas.size(), proveedorId);
        return escalas;
    }


    @Transactional
    public List<Escala> saveMappings(List<Escala> escalas) {
        logger.info("Saving {} scale mappings", escalas.size());

        // Validate that all escalas have escalaInt between 1-5
        for (Escala escala : escalas) {
            if (escala.getEscalaInt() != null &&
                (escala.getEscalaInt() < 1 || escala.getEscalaInt() > 5)) {
                throw new IllegalArgumentException(
                    "Internal scale must be between 1 and 5, got: " + escala.getEscalaInt()
                );
            }
        }

        List<Escala> saved = escalaRepository.saveAll(escalas);
        logger.info("Successfully saved {} scale mappings", saved.size());
        return saved;
    }

    /**
     * Save a single scale mapping
     */
    @Transactional
    public Escala saveMapping(Escala escala) {
        logger.info("Saving scale mapping: {} -> {}", escala.getEscalaExt(), escala.getEscalaInt());

        // Validate escalaInt if present
        if (escala.getEscalaInt() != null &&
            (escala.getEscalaInt() < 1 || escala.getEscalaInt() > 5)) {
            throw new IllegalArgumentException(
                "Internal scale must be between 1 and 5, got: " + escala.getEscalaInt()
            );
        }

        return escalaRepository.save(escala);
    }

    /**
     * Convert internal scale (1-5) to external scale ("Excelente", etc)
     * Used when sending ratings TO the provider
     */
    @Transactional(readOnly = true)
    public String convertToExternal(Integer proveedorId, Integer escalaInt) {
        logger.info("Converting internal scale {} to external for provider {}", escalaInt, proveedorId);

        if (escalaInt < 1 || escalaInt > 5) {
            throw new IllegalArgumentException("Internal scale must be between 1 and 5, got: " + escalaInt);
        }

        Escala escala = escalaRepository.findByInternal(proveedorId, escalaInt)
            .orElseThrow(() -> new EscalaNotFoundException(
                "No mapping found for provider " + proveedorId + " and internal scale " + escalaInt
            ));

        logger.info("Converted {} -> {}", escalaInt, escala.getEscalaExt());
        return escala.getEscalaExt();
    }

    /**
     * Get unmapped scales for a provider (escalaInt = NULL)
     * Used by frontend to show which scales need mapping
     */
    @Transactional(readOnly = true)
    public List<Escala> getUnmappedScales(Integer proveedorId) {
        logger.info("Getting unmapped scales for provider: {}", proveedorId);

        List<Escala> allEscalas = escalaRepository.findByProveedor(proveedorId);
        List<Escala> unmapped = allEscalas.stream()
            .filter(e -> e.getEscalaInt() == null)
            .toList();

        logger.info("Found {} unmapped scales for provider {}", unmapped.size(), proveedorId);
        return unmapped;
    }

    /**
     * Check if all scales for a provider are mapped
     */
    @Transactional(readOnly = true)
    public boolean areAllScalesMapped(Integer proveedorId) {
        List<Escala> unmapped = getUnmappedScales(proveedorId);
        boolean allMapped = unmapped.isEmpty();

        logger.info("Provider {} scales mapping status: {}",
            proveedorId, allMapped ? "Complete" : "Incomplete");

        return allMapped;
    }
}
