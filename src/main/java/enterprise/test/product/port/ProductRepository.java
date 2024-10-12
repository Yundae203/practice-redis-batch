package enterprise.test.product.port;


import enterprise.test.product.domain.Product;

public interface ProductRepository {

    void save(Product product);

    Product findById(Long id);

    boolean existsById(Long id);

    void update(Product product);
}
