package enterprise.test.infra.batch;

import enterprise.test.infra.redis.ReviewCache;

public record KeyCache(Long key, ReviewCache cache) {
}
