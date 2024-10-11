package enterprise.test.review.repository;

import enterprise.test.review.domain.Review;
import enterprise.test.review.infra.ReviewEntity;
import enterprise.test.review.infra.ReviewJpaRepository;
import enterprise.test.review.port.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public void save(Review review) {
        reviewJpaRepository.save(ReviewEntity.from(review));
    }

    @Override
    public Review findById(Long id) {
        ReviewEntity entity = reviewJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found"));
        return entity.toModel();
    }

    @Override
    public boolean existsByProductIdAndUserId(Long productId, Long userId) {
        return reviewJpaRepository.existsByProductIdAndUserId(productId, userId);
    }

    @Override
    public Slice<Review> findNextPageByProductId(Long productId, Long cursor, Pageable pageable) {
        return reviewJpaRepository.findNextPageByProductId(productId, cursor, pageable)
                .map(ReviewEntity::toModel);
    }

    @Override
    public Slice<Review> findFirstPageByProductId(Long productId, Pageable pageable) {
        return null;
    }


}
