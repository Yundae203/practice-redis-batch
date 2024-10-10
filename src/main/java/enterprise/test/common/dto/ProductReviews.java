package enterprise.test.common.dto;

import enterprise.test.product.domain.Product;
import enterprise.test.review.domain.Review;
import enterprise.test.review.dto.ReviewResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductReviews(
        int totalCount,
        double score,
        int cursor,
        List<ReviewResponse> reviews
) {

    public static ProductReviews of(Product product, int cursor, List<Review> review) {
        return ProductReviews.builder()
                .totalCount(product.getReviewCount())
                .score(product.getScore())
                .cursor(cursor)
                .reviews(review.stream().map(ReviewResponse::from).toList())
                .build();
    }
}
