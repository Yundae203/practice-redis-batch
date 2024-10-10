package enterprise.test.infra.batch;

import enterprise.test.infra.redis.RedisService;
import enterprise.test.infra.redis.ReviewCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Slf4j
public class RedisKeyItemReader implements ItemReader<KeyCache> {
    private Iterator<Long> redisKeys;
    private final Map<Long, KeyCache> cacheMap = new HashMap<>();
    private final RedisService<ReviewCache, Long> reviewRedisService;

    // 생성자에서 ReviewRedisService를 주입 받음
    public RedisKeyItemReader(RedisService<ReviewCache, Long> reviewRedisService) {
        this.reviewRedisService = reviewRedisService;
    }

    @Override
    public KeyCache read() {
        log.debug("Reading keys from Redis");
        // 처음 실행될 때 Redis에서 키들을 가져와 Iterator에 저장
        log.info("redisKeys: {}", redisKeys);
        if (redisKeys == null) {
            Set<Long> keys = reviewRedisService.keys();
            redisKeys = keys.iterator();
            log.debug("Found {} keys", keys.size());
        }

        // Redis에서 가져온 키를 처리
        if (redisKeys.hasNext()) {
            Long key = redisKeys.next();
            KeyCache value = cacheMap.get(key);

            // Cache에 값이 없으면 Redis에서 가져와 저장
            if (value == null) {
                ReviewCache cache = reviewRedisService.getAndRemoveValue(key);
                value = new KeyCache(key, cache);
                cacheMap.put(key, value);
            }

            return value;  // 처리할 KeyCache 반환
        }

        // 모든 키를 처리한 후 null 반환하여 배치 종료
        return null;
    }

    // 필요 시 추가 메서드나 초기화 작업을 수행할 수 있는 메서드 추가 가능
    public void reset() {
        this.redisKeys = null;  // 새로 작업을 시작할 때 redisKeys를 초기화
    }
}
