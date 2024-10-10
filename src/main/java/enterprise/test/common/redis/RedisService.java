package enterprise.test.common.redis;

import org.springframework.stereotype.Service;

import java.util.Set;

public interface RedisService<T, K> {

    void setValue(K key, T value);

    T getValue(K key);

    void deleteValue(K key);

    Set<K> keys();

    T getAndRemoveValue(K key);

}
