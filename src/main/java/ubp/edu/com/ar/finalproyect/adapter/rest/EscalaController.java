package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Escala;
import ubp.edu.com.ar.finalproyect.service.EscalaService;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing rating scale mappings
 */
@RestController
@RequestMapping("/api/escalas")
public class EscalaController {

    private static final Logger logger = LoggerFactory.getLogger(EscalaController.class);

    @Autowired
    private EscalaService escalaService;

    /**
     * GET /api/escalas/proveedor/{id}
     * Get all scale mappings for a provider
     * Used by frontend to display all scales (mapped and unmapped)
     */
    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<Escala>> getEscalasByProveedor(@PathVariable Integer proveedorId) {
        logger.info("GET /api/escalas/proveedor/{} - Getting all scales", proveedorId);

        List<Escala> escalas = escalaService.getMappingsByProveedor(proveedorId);

        logger.info("Found {} scale mappings for provider {}", escalas.size(), proveedorId);
        return ResponseEntity.ok(escalas);
    }


    @GetMapping("/proveedor/{proveedorId}/unmapped")
    public ResponseEntity<List<Escala>> getUnmappedEscalas(@PathVariable Integer proveedorId) {
        logger.info("GET /api/escalas/proveedor/{}/unmapped - Getting unmapped scales", proveedorId);

        List<Escala> unmapped = escalaService.getUnmappedScales(proveedorId);

        logger.info("Found {} unmapped scales for provider {}", unmapped.size(), proveedorId);
        return ResponseEntity.ok(unmapped);
    }

    @GetMapping("/proveedor/{proveedorId}/status")
    public ResponseEntity<Map<String, Object>> getMappingStatus(@PathVariable Integer proveedorId) {
        logger.info("GET /api/escalas/proveedor/{}/status - Checking mapping status", proveedorId);

        boolean allMapped = escalaService.areAllScalesMapped(proveedorId);
        List<Escala> all = escalaService.getMappingsByProveedor(proveedorId);
        List<Escala> unmapped = escalaService.getUnmappedScales(proveedorId);

        Map<String, Object> status = Map.of(
            "proveedorId", proveedorId,
            "totalScales", all.size(),
            "mappedScales", all.size() - unmapped.size(),
            "unmappedScales", unmapped.size(),
            "allMapped", allMapped
        );

        logger.info("Mapping status for provider {}: {}/{} mapped",
            proveedorId, all.size() - unmapped.size(), all.size());

        return ResponseEntity.ok(status);
    }

    @PostMapping
    public ResponseEntity<List<Escala>> saveMappings(@RequestBody List<Escala> escalas) {
        logger.info("POST /api/escalas - Saving {} scale mappings", escalas.size());

        List<Escala> saved = escalaService.saveMappings(escalas);

        logger.info("Successfully saved {} scale mappings", saved.size());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Escala> updateMapping(
        @PathVariable Integer id,
        @RequestBody Escala escala
    ) {
        logger.info("PUT /api/escalas/{} - Updating scale mapping", id);

        escala.setIdEscala(id);  // Ensure ID matches path parameter
        Escala updated = escalaService.saveMapping(escala);

        logger.info("Successfully updated scale mapping {}", id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/convert/{proveedorId}")
    public ResponseEntity<Map<String, String>> convertToExternal(
        @PathVariable Integer proveedorId,
        @RequestParam Integer escalaInt
    ) {
        logger.info("GET /api/escalas/convert/{} - Converting {} to external",
            proveedorId, escalaInt);

        String external = escalaService.convertToExternal(proveedorId, escalaInt);

        logger.info("Converted {} -> {} for provider {}", escalaInt, external, proveedorId);

        return ResponseEntity.ok(Map.of(
            "proveedorId", proveedorId.toString(),
            "escalaInt", escalaInt.toString(),
            "escalaExt", external
        ));
    }
}
