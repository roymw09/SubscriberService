package org.ac.cst8277.williams.roy;

import org.ac.cst8277.williams.roy.model.Content;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@SpringBootApplication
@EnableDiscoveryClient
public class SubscriberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriberServiceApplication.class, args);
    }

    @Bean
    public ReactiveRedisOperations<String, Content> contentTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        RedisSerializer<Content> valueSerializer = new Jackson2JsonRedisSerializer<>(Content.class);
        RedisSerializationContext<String, Content> serializationContext = RedisSerializationContext.<String, Content>newSerializationContext(RedisSerializer.string())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<String, Content>(lettuceConnectionFactory, serializationContext);
    }

}
