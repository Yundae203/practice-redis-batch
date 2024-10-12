package enterprise.test.review.dto;

import enterprise.test.review.domain.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

public record ReviewRequest(
        Long userId,
        @Min(1) @Max(5)
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
