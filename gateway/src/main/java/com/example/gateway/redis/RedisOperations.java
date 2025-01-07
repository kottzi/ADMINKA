package com.example.gateway.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisOperations<T> {

    private final ObjectMapper objectMapper;
    private final RedisClient redisClient;


    public List<T> get(String key) {
        String value = redisClient.connect()
                .sync()
                .get(key);
        if (value != null) {
            try {
                return objectMapper.readValue(value, new TypeReference<List<T>>() {});
            } catch (JsonProcessingException e) {
                log.error("Ошибка парсинга из кэша!",  e);
            }
        }
        return List.of();
    }

    public void save(String key, List<T> toCache) {
        try {
            redisClient.connect()
                    .sync()
                    .set(key, objectMapper.writeValueAsString(toCache));
        } catch (JsonProcessingException e) {
            log.error("Ошибка записи в кэш!Я", e);
        }
    }

    public void delete(String key) {
        redisClient.connect().sync().del(key);
    }
}
