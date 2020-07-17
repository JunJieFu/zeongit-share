package com.zeongit.share.component

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * @author fjj
 * redis服务
 */
@Component
class RedisComponent(private val redisTemplate: StringRedisTemplate) {
    fun set(key: String, value: String, expire: Long, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit)
    }

    fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }
}