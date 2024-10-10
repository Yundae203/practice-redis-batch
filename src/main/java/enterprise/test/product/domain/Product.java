package enterprise.test.product.domain;

import lombok.Getter;

@Getter
public class Product {

    private Long id;
    private int reviewCount;
    private double score;

    // construct[s]
    private Product(Builder builder) {
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

        public Product build() {
            return new Product(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    // construct[e]

    public void updateCountAndScore(int count, double score) {
        incrementReviewCount(count);
        calculateScore(score);
    }

    private void incrementReviewCount(int count) {
        this.reviewCount += count;
    }

    private void calculateScore(double score) {
        this.score = (this.score + score) / 2;
    }
}
