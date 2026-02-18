package com.app.kh.camnextgen.shared.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final RateLimitProperties properties;
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public RateLimitService(RateLimitProperties properties) {
        this.properties = properties;
    }

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, this::newBucket);
    }

    public Bucket newBucket(String key) {
        if (properties == null) {
            throw new IllegalStateException("RateLimitProperties not initialized");
        }
        Bandwidth limit = Bandwidth.builder()
                .capacity(properties.getCapacity())
                .refillGreedy(properties.getRefillTokens(), properties.getRefillDuration())
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
