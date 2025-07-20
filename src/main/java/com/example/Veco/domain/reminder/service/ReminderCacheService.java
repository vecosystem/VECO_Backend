package com.example.Veco.domain.reminder.service;

import com.example.Veco.domain.reminder.util.RedisKeyUtil;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    public static final Duration TTL = Duration.ofHours(24);

    public void cacheTodayTypeIds(Category type, List<Long> typeIds) {
        String key = RedisKeyUtil.todayReminderKey(type);
        redisTemplate.opsForValue().set(key, typeIds, TTL);
    }

    public List<Long> getTodayTypeIds(Category type) {
        String key = RedisKeyUtil.todayReminderKey(type);
        return (List<Long>) redisTemplate.opsForValue().get(key);
    }

    public void clearTypeIds(Category type, LocalDate date) {
        String key = RedisKeyUtil.customReminderKey(type, date);
        redisTemplate.delete(key);
    }

}


