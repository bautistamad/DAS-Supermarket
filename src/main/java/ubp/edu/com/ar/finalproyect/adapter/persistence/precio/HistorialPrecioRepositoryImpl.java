package ubp.edu.com.ar.finalproyect.adapter.persistence.precio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.HistorialPrecio;
import ubp.edu.com.ar.finalproyect.port.HistorialPrecioRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class HistorialPrecioRepositoryImpl implements HistorialPrecioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<HistorialPrecio> findByProducto(Integer codigoBarra) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", codigoBarra);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_precio_history_by_product")
                .withSchemaName("dbo")
                .returningResultSet("precios", BeanPropertyRowMapper.newInstance(HistorialPrecioEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<HistorialPrecioEntity> result = (List<HistorialPrecioEntity>) out.get("precios");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialPrecio> findCurrentPrecioByProductoAndProveedor(Integer codigoBarra, Long idProveedor) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", codigoBarra)
                .addValue("idProveedor", idProveedor);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_current_precio_by_product_provider")
                .withSchemaName("dbo")
                .returningResultSet("precios", BeanPropertyRowMapper.newInstance(HistorialPrecioEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<HistorialPrecioEntity> result = (List<HistorialPrecioEntity>) out.get("precios");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    public HistorialPrecio getCurrentPrice(Integer codigoBarra, Integer idProveedor) {
        Optional<HistorialPrecio> precio = findCurrentPrecioByProductoAndProveedor(codigoBarra, idProveedor.longValue());
        return precio.orElse(null);
    }

    @Override
    public HistorialPrecio save(HistorialPrecio historialPrecio) {
        // This method is not needed for now as we use syncPrecio
        throw new UnsupportedOperationException("Use syncPrecio for price synchronization");
    }

    @Override
    @Transactional
    public Map<String, Object> syncPrecio(Integer codigoBarra, Float precio, Integer idProveedor) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", codigoBarra)
                .addValue("precio", precio)
                .addValue("idProveedor", idProveedor);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_sync_precio_from_proveedor")
                .withSchemaName("dbo");

        Map<String, Object> result = jdbcCall.execute(in);

        // The SP returns a result set, extract the first row
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }

        // Fallback if no result
        return Map.of(
                "codigoBarra", codigoBarra,
                "status", "ERROR",
                "action", "NO_RESULT",
                "errorMessage", "Stored procedure did not return results"
        );
    }

    // Helper: Entity → Domain
    private HistorialPrecio toDomain(HistorialPrecioEntity entity) {
        HistorialPrecio precio = new HistorialPrecio(
                entity.getCodigoBarra(),
                entity.getIdProveedor(),
                entity.getPrecio(),
                entity.getFechaInicio(),
                entity.getFechaFin()
        );

        if (entity.getProveedorNombre() != null) {
            precio.setProveedorNombre(entity.getProveedorNombre());
        }
        if (entity.getProductoNombre() != null) {
            precio.setProductoNombre(entity.getProductoNombre());
        }

        return precio;
    }
}
