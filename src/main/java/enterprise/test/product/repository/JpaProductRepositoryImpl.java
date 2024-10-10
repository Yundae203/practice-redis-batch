package enterprise.test.product.repository;

import enterprise.test.product.domain.Product;
import enterprise.test.product.infra.jpa.ProductEntity;
import enterprise.test.product.infra.jpa.ProductJpaRepository;
import enterprise.test.product.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class JpaProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public void save(Product product) {
        productJpaRepository.save(ProductEntity.from(product));
    }

    @Override
    public Product findById(Long id) {
        ProductEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        return entity.toModel();
    }

    @Override
    public boolean existsById(Long id) {
        return productJpaRepository.existsById(id);
    }

    @Override
    public void update(Product product) {
        if (!existsById(product.getId())) {
            throw new NoSuchElementException("Product not found");
        }
        save(product);
    }

}