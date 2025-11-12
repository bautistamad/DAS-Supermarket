package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.port.ProveedorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProveedorRepositoryImpl implements ProveedorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // TO DO
    // MODIFICAR DB
    @Override
    @Transactional
    public Proveedor save(Proveedor proveedor) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", proveedor.getId())
                .addValue("nombre", proveedor.getName())
                .addValue("apiEndpoint", proveedor.getApiEndpoint())
                .addValue("tipoServicio", proveedor.getTipoServicio())
                .addValue("apiKey", proveedor.getApiKey());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_save_provider")
                .withSchemaName("dbo")
                .returningResultSet("providers", BeanPropertyRowMapper.newInstance(ProveedorEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProveedorEntity> result = (List<ProveedorEntity>) out.get("providers");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return proveedor;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Proveedor> findById(Integer id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", id);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_provider_by_id")
                .withSchemaName("dbo")
                .returningResultSet("providers", BeanPropertyRowMapper.newInstance(ProveedorEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProveedorEntity> result = (List<ProveedorEntity>) out.get("providers");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proveedor> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_providers")
                .withSchemaName("dbo")
                .returningResultSet("providers", BeanPropertyRowMapper.newInstance(ProveedorEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<ProveedorEntity> result = (List<ProveedorEntity>) out.get("providers");

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
    private Proveedor toDomain(ProveedorEntity entity) {
        return new Proveedor(
                entity.getId(),
                entity.getNombre(),
                entity.getApiEndpoint(),
                entity.getTipoServicio(),
                entity.getTipoServicioNombre(),
                entity.getApiKey()
        );
    }

    // Helper: Domain → Entity
    private ProveedorEntity toEntity(Proveedor proveedor) {
        return new ProveedorEntity(
                proveedor.getId(),
                proveedor.getName(),
                proveedor.getApiEndpoint(),
                proveedor.getTipoServicio(),
                proveedor.getTipoServicioNombre(),
                proveedor.getApiKey()
        );
    }
}
