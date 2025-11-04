package ubp.edu.com.ar.finalproyect.adapter.persistence.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Order;
import ubp.edu.com.ar.finalproyect.port.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Order save(Order order) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("estado", order.getEstadoId())
                .addValue("proveedor", order.getProveedorId())
                .addValue("puntuacion", order.getPuntuacion())
                .addValue("fechaEntrega", order.getFechaEntrega())
                .addValue("evaluacion", order.getEvaluacion());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_create_pedido")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(OrderEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<OrderEntity> result = (List<OrderEntity>) out.get("pedidos");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return order;
    }

    @Override
    @Transactional
    public Order update(Order order) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", order.getId())
                .addValue("estado", order.getEstadoId())
                .addValue("puntuacion", order.getPuntuacion())
                .addValue("fechaEntrega", order.getFechaEntrega())
                .addValue("evaluacion", order.getEvaluacion());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_pedido")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(OrderEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<OrderEntity> result = (List<OrderEntity>) out.get("pedidos");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Integer id) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("id", id);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_pedido_by_id")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(OrderEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<OrderEntity> result = (List<OrderEntity>) out.get("pedidos");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_pedidos")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(OrderEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<OrderEntity> result = (List<OrderEntity>) out.get("pedidos");

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
    public List<Order> findByProviderId(Integer providerId) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("proveedorId", providerId);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_pedidos_by_proveedor")
                .withSchemaName("dbo")
                .returningResultSet("pedidos", BeanPropertyRowMapper.newInstance(OrderEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<OrderEntity> result = (List<OrderEntity>) out.get("pedidos");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    // Helper: Entity → Domain
    private Order toDomain(OrderEntity entity) {
        Order order = new Order(
                entity.getId(),
                entity.getEstado(),
                entity.getProveedor(),
                entity.getPuntuacion(),
                entity.getFechaCreada(),
                entity.getFechaEntrega(),
                entity.getEvaluacion(),
                entity.getFechaRegistro()
        );

        if (entity.getEstadoNombre() != null) {
            order.setEstadoNombre(entity.getEstadoNombre());
        }
        if (entity.getEstadoDescripcion() != null) {
            order.setEstadoDescripcion(entity.getEstadoDescripcion());
        }
        if (entity.getProveedorNombre() != null) {
            order.setProveedorNombre(entity.getProveedorNombre());
        }

        return order;
    }

    // Helper: Domain → Entity
    private OrderEntity toEntity(Order order) {
        return new OrderEntity(
                order.getId(),
                order.getEstadoId(),
                order.getProveedorId(),
                order.getPuntuacion(),
                order.getFechaCreada(),
                order.getFechaEntrega(),
                order.getFechaRegistro(),
                order.getEvaluacion()
        );
    }
}
