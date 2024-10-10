package enterprise.test.common.batch;

import enterprise.test.common.redis.RedisService;
import enterprise.test.common.redis.ReviewCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.*;

@Slf4j
public class RedisKeyItemReader implements ItemReader<KeyCache> {
    private List<Long> redisKeys;
    private final Map<Long, KeyCache> tempRepository = new HashMap<>();
    private final RedisService<ReviewCache, Long> reviewRedisService;

    // 생성자에서 ReviewRedisService를 주입 받음
    public RedisKeyItemReader(RedisService<ReviewCache, Long> reviewRedisService) {
        this.reviewRedisService = reviewRedisService;
    }

    @Override
    public KeyCache read() {
        // 처음 실행될 때 Redis에서 키들을 가져와 Iterator에 저장

        // processor, writer 에러를 대비하여 캐싱
        if (redisKeys == null) {
            Set<Long> keys = reviewRedisService.keys();
            redisKeys = keys.stream().toList();

            for (Long key : keys) {
                ReviewCache cache = reviewRedisService.getAndRemoveValue(key);
                tempRepository.put(key, new KeyCache(key, cache));
            }
        }

        // 데이터 읽어서 반환
        for (Long key : redisKeys) {
            if (tempRepository.containsKey(key)) {  // 처리되지 않은 키만 처리
                KeyCache cache = tempRepository.get(key);
                tempRepository.remove(key);  // 처리 완료 후 제거
                return cache;  // 읽은 데이터를 반환
            }
        }

        // 모든 데이터를 읽는데 성공하면 캐시 초기화 후 배치 종료
        reset();
        return null;
    }

    private void reset() {
        redisKeys = null;
        tempRepository.clear();
    }
}
