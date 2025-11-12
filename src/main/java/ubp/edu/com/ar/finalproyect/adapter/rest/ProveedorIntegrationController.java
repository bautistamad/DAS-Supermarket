package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.service.ProveedorIntegrationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/integracion")
public class ProveedorIntegrationController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorIntegrationController.class);

    private final ProveedorIntegrationService integrationService;

    public ProveedorIntegrationController(ProveedorIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping("/{proveedorId}/health")
    public ResponseEntity<Map<String, Object>> checkHealth(@PathVariable Integer proveedorId) {
        logger.info("Received health check request for provider ID: {}", proveedorId);

        boolean isHealthy = integrationService.checkProveedorHealth(proveedorId);

        Map<String, Object> response = new HashMap<>();
        response.put("proveedorId", proveedorId);
        response.put("healthy", isHealthy);
        response.put("status", isHealthy ? "OK" : "FAILED");

        HttpStatus status = isHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        logger.info("Health check for provider ID {} completed with status: {}",
            proveedorId, status);

        return ResponseEntity.status(status).body(response);
    }
}
