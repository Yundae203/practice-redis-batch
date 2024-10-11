package enterprise.test.application.service;

import enterprise.test.common.dto.ProductReviews;
import enterprise.test.common.time.CustomClock;
import enterprise.test.common.redis.RedisService;
import enterprise.test.common.redis.ReviewCache;
import enterprise.test.product.domain.Product;
import enterprise.test.product.service.ProductService;
import enterprise.test.review.domain.Review;
import enterprise.test.review.dto.ReviewRequest;
import enterprise.test.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductReviewService {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final CustomClock<ZonedDateTime> customClock;
    private final RedisService<ReviewCache, Long> redisService;

    @Async
    @Transactional
    public void saveProductReview(ReviewRequest reviewRequest, Long productId, String imageUrl) {
        boolean exists = reviewService.existsByProductIdAndUserId(productId, reviewRequest.userId());
        validate(!exists, "이미 리뷰가 존재하는 상품입니다.");

        Review review = reviewRequest.toModel(imageUrl, productId, customClock.now()); // 리뷰 생성
        redisService.setValue(review.getProductId(), ReviewCache.from(review)); // 리뷰 점수 캐싱

        reviewService.save(review);
    }

    public ProductReviews getProductReviews(Long productId, Long cursor, Integer size) {
        Product product = productService.findById(productId);
        Slice<Review> reviewSlice = reviewService.findAllByProductId(productId, cursor, size);

        ReviewCache cache = redisService.getValue(productId);
        if (cache != null) {
            product.updateCountAndScore(cache.count(), cache.calculatedScore());
        }

        // 리뷰가 하나 이상 존재할 경우
        Long nextCursor = null;
        if (reviewSlice.getNumberOfElements() > 0) {
            nextCursor = reviewSlice.getContent().get(reviewSlice.getNumberOfElements() - 1).getId();
        }

        return ProductReviews.of(product, nextCursor, reviewSlice.getContent());
    }



    public static void validate(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}