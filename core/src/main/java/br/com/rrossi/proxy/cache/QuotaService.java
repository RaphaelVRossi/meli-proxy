package br.com.rrossi.proxy.cache;

import io.quarkus.redis.client.RedisClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@ApplicationScoped
public class QuotaService {

    private final RedisClient redisClient;

    @Inject
    public QuotaService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    void set(String key, Integer value) {
        redisClient.set(Arrays.asList(key, value.toString()));
    }

    Integer get(String key) {
        return redisClient.get(key).toInteger();
    }

    public Integer increment(String key) {
        redisClient.expire(key, "10");
        return redisClient.incr(key).toInteger();
    }
}
