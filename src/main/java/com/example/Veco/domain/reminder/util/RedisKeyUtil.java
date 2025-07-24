package com.example.Veco.domain.reminder.util;

import com.example.Veco.global.enums.Category;

import java.time.LocalDate;

public class RedisKeyUtil {

    private static final String REMIND_PREFIX = "remind";

    public static String todayReminderKey(Category type) {
        return customReminderKey(type, LocalDate.now());
    }

    public static String customReminderKey(Category type, LocalDate date) {
        return String.format("%s:%s:%s", REMIND_PREFIX, type.name(), date);
    }

}