package enterprise.test.product.review.domain;

import java.time.LocalDateTime;

public class Review {

    private Long id;
    private Long userId;
    private int score;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
}
