package ubp.edu.com.ar.finalproyect.adapter.persistence.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ubp.edu.com.ar.finalproyect.domain.Product;
import ubp.edu.com.ar.finalproyect.port.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Product save(Product product) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", product.getBarCode())
                .addValue("nombre", product.getName())
                .addValue("imagen", product.getImage())
                .addValue("stockMinimo", product.getMinStock())
                .addValue("stockMaximo", product.getMaxStock())
                .addValue("stockActual", product.getCurrentStock());

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_save_product")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductEntity> result = (List<ProductEntity>) out.get("products");

        if (result != null && !result.isEmpty()) {
            return toDomain(result.get(0));
        }

        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByBarCode(Integer barCode) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("codigoBarra", barCode);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_product_by_barcode")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductEntity> result = (List<ProductEntity>) out.get("products");

        if (result != null && !result.isEmpty()) {
            return Optional.of(toDomain(result.get(0)));
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_find_all_products")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductEntity.class));

        Map<String, Object> out = jdbcCall.execute();

        @SuppressWarnings("unchecked")
        List<ProductEntity> result = (List<ProductEntity>) out.get("products");

        if (result != null) {
            return result.stream()
                    .map(this::toDomain)
                    .toList();
        }

        return List.of();
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
    public List<Product> findByProviderId(Integer providerId) {
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("idProveedor", providerId);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_get_products_by_provider")
                .withSchemaName("dbo")
                .returningResultSet("products", BeanPropertyRowMapper.newInstance(ProductEntity.class));

        Map<String, Object> out = jdbcCall.execute(in);

        @SuppressWarnings("unchecked")
        List<ProductEntity> result = (List<ProductEntity>) out.get("products");


        // DEBUG LOGGING - Add these lines
        System.out.println("=== DEBUG: findByProviderId ===");
        System.out.println("Provider ID: " + providerId);
        System.out.println("Result is null: " + (result == null));
        if (result != null) {
            System.out.println("Number of ProductEntity objects: " + result.size());
            for (ProductEntity entity : result) {
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

    // Helper: Entity → Domain
    private Product toDomain(ProductEntity entity) {
        Product product = new Product(
                entity.getCodigoBarra(),
                entity.getNombre(),
                entity.getImagen(),
                entity.getStockMinimo(),
                entity.getStockMaximo()
        );
        product.setCurrentStock(entity.getStockActual());
        return product;
    }

    // Helper: Domain → Entity
    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getBarCode(),
                product.getName(),
                product.getImage(),
                product.getMinStock(),
                product.getMaxStock(),
                product.getCurrentStock()
        );
    }
}
