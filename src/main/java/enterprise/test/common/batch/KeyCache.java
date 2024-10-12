package enterprise.test.common.batch;

import enterprise.test.common.redis.ReviewCache;

public record KeyCache(Long key, ReviewCache cache) {
}
