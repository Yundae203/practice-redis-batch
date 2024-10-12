package enterprise.test.review.domain;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class Review {

    private Long id;

    private Long productId;

    private Long userId;
    private int score;
    private String content;
    private String imageUrl;
    private ZonedDateTime createdAt;

    // construct [s]
    private Review(Builder builder) {
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
            validate(score >= 1 && score <=5, "Score must be between 1 and 5");
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

        public Review build() {
            validate(productId != null, "Product cannot be null");
            validate(userId != null, "userId cannot be null");
            validate(content != null && !content.isEmpty(), "Content cannot be null or empty");
            validate(createdAt != null, "CreatedAt cannot be null");
            return new Review(this);
        }

        private static void validate(boolean condition, String message) {
            if (!condition) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    // construct [e]

    // 비즈니스 로직
}
