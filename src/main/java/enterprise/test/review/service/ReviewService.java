package enterprise.test.review.service;

import enterprise.test.review.domain.Review;
import enterprise.test.review.port.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public void save(Review review) {
        reviewRepository.save(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Slice<Review> findByProductId(Long productId, Long cursor, Pageable pageable) {
        return reviewRepository.findByProduct_IdAndIdLessThanOrderByIdDesc(productId, cursor, pageable);
    }

    public boolean existsByProductIdAndUserId(Long productId, Long userId) {
        return reviewRepository.existsByProductIdAndUserId(productId, userId);
    }

}
