package enterprise.test.product.review.infra;

import enterprise.test.product.infra.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "review", indexes = @Index(name = "idx_product_id", columnList = "product_id"))
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private Long userId;
    private int score;
    private String content;
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;

}
