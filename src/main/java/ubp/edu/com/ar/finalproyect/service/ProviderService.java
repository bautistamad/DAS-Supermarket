package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Provider;
import ubp.edu.com.ar.finalproyect.exception.ProviderNotFoundException;
import ubp.edu.com.ar.finalproyect.port.ProviderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {

    private final ProviderRepository repository;

    public ProviderService(ProviderRepository repository) {
        this.repository = repository;
    }

    public Provider createProvider(Provider provider) {
        // Validate input
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        if (provider.getName() == null || provider.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Provider name cannot be null or empty");
        }

        return repository.save(provider);
    }

    public Optional<Provider> getProvider(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Provider ID cannot be null");
        }
        return repository.findById(id);
    }

    public List<Provider> getAllProviders() {
        return repository.findAll();
    }

    public void deleteProvider(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Provider ID cannot be null");
        }

        // Check if provider exists before deleting
        repository.findById(id)
            .orElseThrow(() -> new ProviderNotFoundException(id));

        repository.deleteById(id);
    }
}
