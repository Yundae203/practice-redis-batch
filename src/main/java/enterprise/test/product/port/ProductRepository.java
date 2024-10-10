package enterprise.test.product.repository;


import enterprise.test.product.domain.Product;

public interface ProductRepository {

    Product save(Product product);

    Product findById(String id);

}
