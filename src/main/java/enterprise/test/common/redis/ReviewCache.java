package enterprise.test.common.redis;

import enterprise.test.review.domain.Review;

public record ReviewCache(
        int count,
        int sum
) {
    public static ReviewCache from(Review review) {
        return new ReviewCache(1, review.getScore());
    }

    public double calculatedScore() {
        return (double) sum / count;
    }
}
