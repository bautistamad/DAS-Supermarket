package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Provider;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository {

    Provider save(Provider provider);
    Optional<Provider> findById(Integer id);
    List<Provider> findAll();
    void deleteById(Integer id);
}
