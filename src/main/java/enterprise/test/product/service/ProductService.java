package enterprise.test.product.service;

import enterprise.test.product.domain.Product;
import enterprise.test.product.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Transactional
    public void update(Product product) {
        productRepository.update(product);
    }
}
