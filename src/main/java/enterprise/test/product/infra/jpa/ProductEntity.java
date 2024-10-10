package enterprise.test.product.infra.jpa;

import enterprise.test.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int reviewCount;
    private double score;

    // construct [s]
    protected ProductEntity() {
        // for JPA
    }

    private ProductEntity(Builder builder) {
        this.id = builder.id;
        this.reviewCount = builder.reviewCount;
        this.score = builder.score;
    }

    public static class Builder {

        private Long id;
        private int reviewCount;
        private double score;
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder reviewCount(int reviewCount) {
            this.reviewCount = reviewCount;
            return this;
        }

        public Builder score(double score) {
            this.score = score;
            return this;
        }

        public ProductEntity build() {
            return new ProductEntity(this);
        }

    }
    public static Builder builder() {
        return new Builder();
    }

    // construct [e]

    // covert [s]
    public static ProductEntity from(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .reviewCount(product.getReviewCount())
                .score(product.getScore())
                .build();
    }
    public Product toModel() {
        return Product.builder()
                .id(id)
                .reviewCount(reviewCount)
                .score(score)
                .build();
    }
    // covert [e]
}
