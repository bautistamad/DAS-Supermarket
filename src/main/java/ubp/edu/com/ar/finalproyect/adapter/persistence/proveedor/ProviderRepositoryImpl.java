package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Provider;
import ubp.edu.com.ar.finalproyect.port.ProviderRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProviderRepositoryImpl implements ProviderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Provider save(Provider provider) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", provider.getId())
                .addValue("nombre", provider.getName())
                .addValue("servicio", provider.getService())
                .addValue("tipoServicio", provider.getServiceType())
                .addValue("escala", provider.getScale());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_save_provider")
                .withSchemaName("dbo")
                .returningResultSet("providers", BeanPropertyRowMapper.newInstance(ProviderEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProviderEntity> result = (List<ProviderEntity>) out.get("providers");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return provider;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Provider> findById(Integer id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", id);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_provider_by_id")
                .withSchemaName("dbo")
                .returningResultSet("providers", BeanPropertyRowMapper.newInstance(ProviderEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProviderEntity> result = (List<ProviderEntity>) out.get("providers");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Provider> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_providers")
                .withSchemaName("dbo")
                .returningResultSet("providers", BeanPropertyRowMapper.newInstance(ProviderEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<ProviderEntity> result = (List<ProviderEntity>) out.get("providers");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", id);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_delete_provider")
                .withSchemaName("dbo");

        jdbcCall.execute(in);
    }

    // Helper: Entity → Domain
    private Provider toDomain(ProviderEntity entity) {
        return new Provider(
                entity.getId(),
                entity.getNombre(),
                entity.getServicio(),
                entity.getTipoServicio(),
                entity.getEscala()
        );
    }

    // Helper: Domain → Entity
    private ProviderEntity toEntity(Provider provider) {
        return new ProviderEntity(
                provider.getId(),
                provider.getName(),
                provider.getService(),
                provider.getServiceType(),
                provider.getScale()
        );
    }
}
