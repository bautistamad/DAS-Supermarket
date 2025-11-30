package ubp.edu.com.ar.finalproyect.adapter.persistence.escala;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Escala;
import ubp.edu.com.ar.finalproyect.port.EscalaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Simplified EscalaRepository implementation
 * Only includes essential operations for the rating scale workflow
 */
@Repository
public class EscalaRepositoryImpl implements EscalaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Escala save(Escala escala) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idEscala", escala.getIdEscala())
                .addValue("idProveedor", escala.getIdProveedor())
                .addValue("escalaInt", escala.getEscalaInt())  // Can be NULL for unmapped scales
                .addValue("escalaExt", escala.getEscalaExt())
                .addValue("descripcionExt", escala.getDescripcionExt());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_save_escala")
                .withSchemaName("dbo")
                .returningResultSet("escalas", BeanPropertyRowMapper.newInstance(EscalaEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<EscalaEntity> result = (List<EscalaEntity>) out.get("escalas");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return escala;
    }

    @Override
    @Transactional
    public List<Escala> saveAll(List<Escala> escalas) {
        List<Escala> saved = new ArrayList<>();
        for (Escala escala : escalas) {
            saved.add(save(escala));
        }
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Escala> findByProveedor(Integer idProveedor) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idProveedor", idProveedor);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_escalas_by_proveedor")
                .withSchemaName("dbo")
                .returningResultSet("escalas", BeanPropertyRowMapper.newInstance(EscalaEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<EscalaEntity> result = (List<EscalaEntity>) out.get("escalas");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Escala> findByInternal(Integer idProveedor, Integer escalaInt) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idProveedor", idProveedor)
                .addValue("escalaInt", escalaInt);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_escala_by_internal")
                .withSchemaName("dbo")
                .returningResultSet("escalas", BeanPropertyRowMapper.newInstance(EscalaEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<EscalaEntity> result = (List<EscalaEntity>) out.get("escalas");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public void updatePedidoEvaluacion(Integer idPedido, Integer idEscala) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idPedido", idPedido)
                .addValue("idEscala", idEscala);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_pedido_evaluacion")
                .withSchemaName("dbo");

        jdbcCall.execute(in);
    }

    // Helper: Entity → Domain
    private Escala toDomain(EscalaEntity entity) {
        Escala escala = new Escala();
        escala.setIdEscala(entity.getIdEscala());
        escala.setIdProveedor(entity.getIdProveedor());
        escala.setEscalaInt(entity.getEscalaInt());
        escala.setEscalaExt(entity.getEscalaExt());
        escala.setDescripcionExt(entity.getDescripcionExt());
        return escala;
    }
}
