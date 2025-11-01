package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;

import org.springframework.data.jpa.repository.JpaRepository;
import ubp.edu.com.ar.finalproyect.domain.Provider;

public interface ProviderJpaRepository extends JpaRepository<ProviderEntity, Integer> {

}
