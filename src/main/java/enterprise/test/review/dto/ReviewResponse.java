package enterprise.test.review.dto;

import enterprise.test.review.domain.Review;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record ReviewResponse(
        Long id,
        Long userId,
        int score,
        String content,
        String imageUrl,
        ZonedDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .score(review.getScore())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
