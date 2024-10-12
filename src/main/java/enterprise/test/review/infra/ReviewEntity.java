package enterprise.test.review.infra;

import enterprise.test.review.domain.Review;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Entity
@Table(name = "review", indexes = @Index(name = "idx_product_id", columnList = "product_id"))
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long userId;
    private int score;
    private String content;
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    // construct[s]
    protected ReviewEntity() {
        // for JPA
    }

    private ReviewEntity(Builder builder) {
        this.id = builder.id;
        this.productId = builder.productId;
        this.userId = builder.userId;
        this.score = builder.score;
        this.content = builder.content;
        this.imageUrl = builder.imageUrl;
        this.createdAt = builder.createdAt;
    }

    public static class Builder {
        private Long id;
        private Long productId;
        private Long userId;
        private int score;
        private String content;
        private String imageUrl;
        private ZonedDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder score(int score) {
            this.score = score;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReviewEntity build() {
            return new ReviewEntity(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    // construct[e]

    // convert[s]
    public static ReviewEntity from(Review review) {
        return ReviewEntity.builder()
                .id(review.getId())
                .productId(review.getProductId())
                .userId(review.getUserId())
                .score(review.getScore())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public Review toModel() {
        return Review.builder()
                .id(id)
                .productId(productId)
                .userId(userId)
                .score(score)
                .content(content)
                .imageUrl(imageUrl)
                .createdAt(createdAt)
                .build();
    }
    // convert[e]
}
