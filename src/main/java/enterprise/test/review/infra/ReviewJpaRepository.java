package enterprise.test.review.infra;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    Slice<ReviewEntity> findByProductIdAndIdLessThanOrderByIdDesc(Long productId, Long cursor, Pageable pageable);

    @Query("select r from ReviewEntity r where r.id <= :cursor and r.productId = :productId")
    Slice<ReviewEntity> findNextPageByProductId(Long productId, Long cursor, Pageable pageable);

    Slice<ReviewEntity> findFirstPageByProductId(Long productId, Pageable pageable);
}
