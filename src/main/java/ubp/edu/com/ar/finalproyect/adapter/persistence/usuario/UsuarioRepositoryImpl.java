package ubp.edu.com.ar.finalproyect.adapter.persistence.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Usuario;
import ubp.edu.com.ar.finalproyect.port.UsuarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("username", usuario.getUsername())
                .addValue("email", usuario.getEmail())
                .addValue("passwordHash", usuario.getPasswordHash());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_save_usuario")
                .withSchemaName("dbo")
                .returningResultSet("usuarios",
                    BeanPropertyRowMapper.newInstance(UsuarioEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<UsuarioEntity> result = (List<UsuarioEntity>) out.get("usuarios");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("username", username);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_usuario_by_username")
                .withSchemaName("dbo")
                .returningResultSet("usuarios",
                    BeanPropertyRowMapper.newInstance(UsuarioEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<UsuarioEntity> result = (List<UsuarioEntity>) out.get("usuarios");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        // Not implemented for now - email search not required
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_usuarios")
                .withSchemaName("dbo")
                .returningResultSet("usuarios",
                    BeanPropertyRowMapper.newInstance(UsuarioEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<UsuarioEntity> result = (List<UsuarioEntity>) out.get("usuarios");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    // Helper: Entity → Domain
    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFechaCreacion(),
                entity.getFechaActualizacion()
        );
    }
}
