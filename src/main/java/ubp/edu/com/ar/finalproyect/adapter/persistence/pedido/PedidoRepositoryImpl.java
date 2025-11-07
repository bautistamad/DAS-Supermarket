package ubp.edu.com.ar.finalproyect.adapter.persistence.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.port.PedidoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PedidoRepositoryImpl implements PedidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Pedido save(Pedido pedido) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("estado", pedido.getEstadoId())
                .addValue("proveedor", pedido.getProveedorId())
                .addValue("puntuacion", pedido.getPuntuacion())
                .addValue("fechaEntrega", pedido.getFechaEntrega())
                .addValue("evaluacion", pedido.getEvaluacionEscala());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_create_pedido")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(PedidoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<PedidoEntity> result = (List<PedidoEntity>) out.get("pedidos");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return pedido;
    }

    @Override
    @Transactional
    public Pedido update(Pedido pedido) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", pedido.getId())
                .addValue("estado", pedido.getEstadoId())
                .addValue("puntuacion", pedido.getPuntuacion())
                .addValue("fechaEntrega", pedido.getFechaEntrega())
                .addValue("evaluacion", pedido.getEvaluacionEscala());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_pedido")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(PedidoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<PedidoEntity> result = (List<PedidoEntity>) out.get("pedidos");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return pedido;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Integer id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", id);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_pedido_by_id")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(PedidoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<PedidoEntity> result = (List<PedidoEntity>) out.get("pedidos");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_pedidos")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(PedidoEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<PedidoEntity> result = (List<PedidoEntity>) out.get("pedidos");

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
                .withProcedureName("sp_delete_pedido")
                .withSchemaName("dbo");

        jdbcCall.execute(in);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByProviderId(Integer providerId) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("proveedorId", providerId);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_pedidos_by_proveedor")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(PedidoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<PedidoEntity> result = (List<PedidoEntity>) out.get("pedidos");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoProducto> findProductsByPedidoId(Integer pedidoId) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idPedido", pedidoId);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_products_by_pedido")
                .withSchemaName("dbo")
                .returningResultSet("productos", BeanPropertyRowMapper.newInstance(PedidoProducto.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<PedidoProducto> result = (List<PedidoProducto>) out.get("productos");

        return result != null ? result : List.of();
    }

    // Helper: Entity → Domain
    private Pedido toDomain(PedidoEntity entity) {
        Pedido pedido = new Pedido(
                entity.getId(),
                entity.getEstado(),
                entity.getProveedor(),
                entity.getPuntuacion(),
                entity.getFechaEstimada(),
                entity.getFechaEntrega(),
                entity.getEvaluacionEscala(),
                entity.getFechaRegistro()
        );

        if (entity.getEstadoNombre() != null) {
            pedido.setEstadoNombre(entity.getEstadoNombre());
        }
        if (entity.getEstadoDescripcion() != null) {
            pedido.setEstadoDescripcion(entity.getEstadoDescripcion());
        }
        if (entity.getProveedorNombre() != null) {
            pedido.setProveedorNombre(entity.getProveedorNombre());
        }

        return pedido;
    }

    // Helper: Domain → Entity
    private PedidoEntity toEntity(Pedido pedido) {
        return new PedidoEntity(
                pedido.getId(),
                pedido.getEstadoId(),
                pedido.getProveedorId(),
                pedido.getPuntuacion(),
                pedido.getFechaEstimada(),
                pedido.getFechaEntrega(),
                pedido.getFechaRegistro(),
                pedido.getEvaluacionEscala()
        );
    }
}
