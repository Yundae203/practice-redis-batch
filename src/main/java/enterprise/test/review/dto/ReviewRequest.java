package enterprise.test.review.dto;

import enterprise.test.review.domain.Review;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

public record ReviewRequest(
        Long userId,
        int score,
        String content,
        MultipartFile image
) {

    public Review toModel(String imageUrl, Long productId, ZonedDateTime zoneDateTime) {
        return Review.builder()
                .productId(productId)
                .userId(userId)
                .score(score)
                .content(content)
                .imageUrl(imageUrl)
                .createdAt(zoneDateTime)
                .build();
    }
}
