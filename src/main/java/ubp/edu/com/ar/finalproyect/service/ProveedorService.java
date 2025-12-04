package ubp.edu.com.ar.finalproyect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Escala;
import ubp.edu.com.ar.finalproyect.domain.EscalaDefinicion;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.exception.ProveedorNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

    private final ProveedorRepository repository;
    private final ProveedorIntegrationService integrationService;
    private final EscalaService escalaService;

    public ProveedorService(ProveedorRepository repository,
                           ProveedorIntegrationService integrationService,
                           EscalaService escalaService) {
        this.repository = repository;
        this.integrationService = integrationService;
        this.escalaService = escalaService;
    }

    public Proveedor createProveedor(Proveedor proveedor) {
        // Validate input
        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor cannot be null");
        }
        if (proveedor.getName() == null || proveedor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Proveedor name cannot be null or empty");
        }
        if (proveedor.getApiEndpoint() == null || proveedor.getApiEndpoint().trim().isEmpty()) {
            throw new IllegalArgumentException("API endpoint cannot be null or empty");
        }
        if (proveedor.getTipoServicio() == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        if (proveedor.getClientId() == null || proveedor.getClientId().trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }
        if (proveedor.getApiKey() == null || proveedor.getApiKey().trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        logger.info("Validating provider connection before creating: {}", proveedor.getName());

        boolean isHealthy = integrationService.checkProveedorHealthDirect(
            proveedor.getApiEndpoint(),
            proveedor.getClientId(),
            proveedor.getApiKey(),
            proveedor.getTipoServicio()
        );

        if (!isHealthy) {
            String errorMsg = String.format(
                "Cannot create provider '%s': Health check failed. Unable to connect to endpoint '%s'",
                proveedor.getName(),
                proveedor.getApiEndpoint()
            );
            logger.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        logger.info("Health check passed for provider: {}", proveedor.getName());

        // Save provider
        Proveedor saved = repository.save(proveedor);
        logger.info("Provider saved with ID: {}", saved.getId());

        // Fetch and save rating scale from provider (unmapped)
        try {
            logger.info("Fetching rating scale from provider {}", saved.getId());

            List<EscalaDefinicion> escalasExternas =
                integrationService.fetchEscalaFromProveedor(saved.getId());

            if (escalasExternas != null && !escalasExternas.isEmpty()) {
                logger.info("Successfully fetched {} scale values from provider {}",
                    escalasExternas.size(), saved.getId());

                // Save scales WITHOUT mapping (escalaInt = null)
                // User will map them later via frontend
                for (EscalaDefinicion def : escalasExternas) {
                    Escala escala = new Escala();
                    escala.setIdProveedor(saved.getId());
                    escala.setEscalaInt(null);  // NOT YET MAPPED
                    escala.setEscalaExt(def.getValor());
                    escala.setDescripcionExt(def.getDescripcion());

                    escalaService.saveMapping(escala);
                }

                logger.info("Saved {} unmapped scale values for provider {}",
                    escalasExternas.size(), saved.getId());
            } else {
                logger.warn("No scale values received from provider {}", saved.getId());
            }

        } catch (Exception e) {
            logger.error("Error fetching scale from provider {}: {}",
                saved.getId(), e.getMessage());
            // Continue anyway - scales can be created manually later
        }

        return saved;
    }

    public Optional<Proveedor> getProveedor(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Proveedor ID cannot be null");
        }
        return repository.findById(id);
    }

    public List<Proveedor> getAllProveedores() {
        return repository.findAll();
    }

    public void deleteProveedor(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Proveedor ID cannot be null");
        }

        // Check if provider exists before deleting
        repository.findById(id)
            .orElseThrow(() -> new ProveedorNotFoundException(id));

        repository.deleteById(id);
    }

}
