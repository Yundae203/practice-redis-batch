package enterprise.test.collabo;

import enterprise.test.common.CustomClock;
import enterprise.test.infra.redis.RedisService;
import enterprise.test.infra.redis.ReviewCache;
import enterprise.test.product.domain.Product;
import enterprise.test.product.service.ProductService;
import enterprise.test.review.domain.Review;
import enterprise.test.review.dto.ReviewRequest;
import enterprise.test.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
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
        redisService.keys();

        reviewService.save(review);
    }

    public static void validate(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
