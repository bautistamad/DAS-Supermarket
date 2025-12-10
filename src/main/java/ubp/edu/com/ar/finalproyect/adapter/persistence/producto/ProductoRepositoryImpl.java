package ubp.edu.com.ar.finalproyect.adapter.persistence.producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.port.ProductoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductoRepositoryImpl implements ProductoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Producto save(Producto producto) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", producto.getCodigoBarra())
                .addValue("nombre", producto.getNombre())
                .addValue("imagen", producto.getImage())
                .addValue("stockMinimo", producto.getMinStock())
                .addValue("stockMaximo", producto.getMaxStock())
                .addValue("stockActual", producto.getActualStock());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_save_product")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductoEntity> result = (List<ProductoEntity>) out.get("products");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return producto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findByBarCode(Integer barCode) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", barCode);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_product_by_barcode")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductoEntity> result = (List<ProductoEntity>) out.get("products");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_products")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductoEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<ProductoEntity> result = (List<ProductoEntity>) out.get("products");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    @Override
    @Transactional
    public Producto update(Producto producto) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", producto.getCodigoBarra())
                .addValue("nombre", producto.getNombre())
                .addValue("imagen", producto.getImage())
                .addValue("stockMinimo", producto.getMinStock())
                .addValue("stockMaximo", producto.getMaxStock())
                .addValue("stockActual", producto.getActualStock());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_update_product")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductoEntity> result = (List<ProductoEntity>) out.get("products");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return producto;
    }

    @Override
    @Transactional
    public void deleteByBarCode(Integer barCode) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", barCode);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_delete_product")
                .withSchemaName("dbo");

        jdbcCall.execute(in);
    }

    @Override
    public List<Producto> findByProviderId(Integer providerId) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idProveedor", providerId);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_products_by_provider")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductoEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductoEntity> result = (List<ProductoEntity>) out.get("products");


        // DEBUG LOGGING - Add these lines
        System.out.println("=== DEBUG: findByProviderId ===");
        System.out.println("Proveedor ID: " + providerId);
        System.out.println("Result is null: " + (result == null));
        if (result != null) {
            System.out.println("Number of ProductoEntity objects: " + result.size());
            for (ProductoEntity entity : result) {
                System.out.println("  - Entity: barCode=" + entity.getCodigoBarra() +
                        ", name=" + entity.getNombre());
            }
        }

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    @Override
    public List<Producto> findProductosBajoStock() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_productos_bajo_stock")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductoEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<ProductoEntity> result = (List<ProductoEntity>) out.get("products");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
    }

    // Helper: Entity → Domain
    private Producto toDomain(ProductoEntity entity) {
        Producto producto = new Producto(
                entity.getCodigoBarra(),
                entity.getNombre(),
                entity.getImagen(),
                entity.getStockMinimo(),
                entity.getStockMaximo(),
                entity.getStockActual()
        );
        producto.setActualStock(entity.getStockActual());
        if (entity.getFechaActualizacion() != null) {
            producto.setUpdateDate(entity.getFechaActualizacion());
        }
        if (entity.getEstado() != null) {
            producto.setEstadoId(entity.getEstado());
        }
        if (entity.getEstadoNombre() != null) {
            producto.setEstadoNombre(entity.getEstadoNombre());
        }
        if (entity.getEstadoDescripcion() != null) {
            producto.setEstadoDescripcion(entity.getEstadoDescripcion());
        }
        return producto;
    }

    // Helper: Domain → Entity
    private ProductoEntity toEntity(Producto producto) {
        return new ProductoEntity(
                producto.getCodigoBarra(),
                producto.getNombre(),
                producto.getImage(),
                producto.getMinStock(),
                producto.getMaxStock(),
                producto.getActualStock()
        );
    }
}
