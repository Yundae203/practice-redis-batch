package enterprise.test.review.port;

import enterprise.test.review.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepository {

    void save(Review review);

    Review findById(Long id);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    Slice<Review> findNextPageByProductId(Long productId, Long cursor, Pageable pageable);

    Slice<Review> findFirstPageByProductId(Long productId, Pageable pageable);
}
