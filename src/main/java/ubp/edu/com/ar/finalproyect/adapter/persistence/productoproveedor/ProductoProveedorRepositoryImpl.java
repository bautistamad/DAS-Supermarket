package ubp.edu.com.ar.finalproyect.adapter.persistence.productoproveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.ProductoProveedor;
import ubp.edu.com.ar.finalproyect.port.ProductoProveedorRepository;

import java.util.List;
import java.util.Map;

@Repository
public class ProductoProveedorRepositoryImpl implements ProductoProveedorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public ProductoProveedor assign(ProductoProveedor assignment) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idProveedor", assignment.getIdProveedor())
                .addValue("codigoProducto", assignment.getCodigoBarra())
                .addValue("codigoBarraProveedor", assignment.getCodigoBarraProveedor())
                .addValue("estado", assignment.getEstado());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_assign_product_to_provider")
                .withSchemaName("dbo")
                .returningResultSet("assignments", BeanPropertyRowMapper.newInstance(ProductoProveedorEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductoProveedorEntity> result = (List<ProductoProveedorEntity>) out.get("assignments");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return assignment;
    }

    @Override
    @Transactional
    public void unassign(Integer codigoBarra, Integer idProveedor) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idProveedor", idProveedor)
                .addValue("codigoProducto", codigoBarra);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_unassign_product_from_provider")
                .withSchemaName("dbo");

        jdbcCall.execute(in);
    }

    // Helper: Entity → Domain
    private ProductoProveedor toDomain(ProductoProveedorEntity entity) {
        ProductoProveedor domain = new ProductoProveedor();
        domain.setIdProveedor(entity.getIdProveedor());
        domain.setCodigoBarra(entity.getCodigoBarra());
        domain.setCodigoBarraProveedor(entity.getCodigoBarraProveedor());
        domain.setEstado(entity.getEstado());
        domain.setFechaActualizacion(entity.getFechaActualizacion());
        domain.setProductoNombre(entity.getProductoNombre());
        domain.setProveedorNombre(entity.getProveedorNombre());
        return domain;
    }
}
