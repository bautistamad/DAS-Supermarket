package ubp.edu.com.ar.finalproyect.adapter.persistence.product;

import org.springframework.stereotype.Repository;
import ubp.edu.com.ar.finalproyect.domain.Product;
import ubp.edu.com.ar.finalproyect.port.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Product> findByBarCode(Integer barCode) {
        return jpaRepository.findById(barCode).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByBarCode(Integer barCode) {
        jpaRepository.deleteById(barCode);
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
