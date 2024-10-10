package enterprise.test.review.infra;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    Slice<ReviewEntity> findByProductIdAndIdLessThanOrderByIdDesc(Long productId, Long cursor, Pageable pageable);

}
