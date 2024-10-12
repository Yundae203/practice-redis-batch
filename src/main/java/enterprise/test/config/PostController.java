package enterprise.test.config;

import enterprise.test.product.domain.Product;
import enterprise.test.product.port.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostController {

    private final ProductRepository productRepository;

    @PostConstruct
    public void init() {
        for (int i=0; i < 10; i++) {
            Product product = Product.builder()
                    .reviewCount(0)
                    .score(0.0)
                    .build();
            productRepository.save(product);
        }

    }
}
