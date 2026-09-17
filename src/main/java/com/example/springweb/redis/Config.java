package com.example.springweb.redis;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class Config extends CachingConfigurerSupport {

    @Value(value="${spring.redis.host}")
    private String host;

    @Value(value = "${spring.redis.port}")
    private String port;

    @Value(value = "${redis.timeout}")
    private String timeout;

    @Bean
    JedisConnectionFactory jedisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration  = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(6379);

        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfig = JedisClientConfiguration.builder();
        jedisClientConfig.connectTimeout(Duration.ofSeconds(Integer.parseInt(timeout)));

        return new JedisConnectionFactory(redisStandaloneConfiguration,
             jedisClientConfig.build());
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
