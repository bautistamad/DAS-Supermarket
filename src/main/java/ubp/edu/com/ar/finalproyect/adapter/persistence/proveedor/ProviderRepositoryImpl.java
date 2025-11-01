package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;

import org.springframework.stereotype.Repository;
import ubp.edu.com.ar.finalproyect.domain.Provider;
import ubp.edu.com.ar.finalproyect.port.ProviderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProviderRepositoryImpl implements ProviderRepository {

    private final ProviderJpaRepository jpaRepository;

    public ProviderRepositoryImpl(ProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    @Override
    public Provider save(Provider provider) {
        ProviderEntity entity = toEntity(provider);
        ProviderEntity saved = jpaRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<Provider> findById(Integer id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Provider> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }

    // Helper: Entity -> Domain

    private Provider toDomain(ProviderEntity entity) {
        Provider provider = new Provider(
                entity.getId(),
                entity.getNombre(),
                entity.getServicio(),
                entity.getTipoServicio(),
                entity.getEscala()
        );
        return provider;
    }

    // Helper : Domain -> Entity

    private ProviderEntity toEntity(Provider provider) {
        ProviderEntity entity = new ProviderEntity(
                provider.getId(),
                provider.getName(),
                provider.getService(),
                provider.getServiceType(),
                provider.getScale()
        );

        return entity;
    }


}
