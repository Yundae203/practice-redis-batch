package enterprise.test.review.service;

import enterprise.test.review.domain.Review;
import enterprise.test.review.port.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    public Slice<Review> findAllByProductId(Long productId, Long cursor, Integer size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        return reviewRepository.findNextPageByProductId(productId, cursor, pageable);
    }

    public boolean existsByProductIdAndUserId(Long productId, Long userId) {
        return reviewRepository.existsByProductIdAndUserId(productId, userId);
    }

}
