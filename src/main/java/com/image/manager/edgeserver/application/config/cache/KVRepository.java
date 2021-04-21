package com.image.manager.edgeserver.application.config.cache;

import java.util.Optional;

/**
 * Created by surjak on 21.04.2021
 */
public interface KVRepository<K, V> {
    void save(K key, V value);
    Optional<V> find(K key);
    void delete(K key);
}
