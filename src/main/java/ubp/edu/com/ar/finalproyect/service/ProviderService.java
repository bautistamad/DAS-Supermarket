package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Provider;
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
        return repository.save(provider);
    }

    public Optional<Provider> getProvider(Integer id) {
        return repository.findById(id);
    }

    public List<Provider> getAllProviders() {
        return repository.findAll();
    }

    public void deleteProvider(Integer id) {
        repository.deleteById(id);
    }
}
