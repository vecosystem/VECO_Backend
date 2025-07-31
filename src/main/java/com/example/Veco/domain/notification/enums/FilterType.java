package com.example.Veco.domain.notification.enums;

import com.example.Veco.domain.notification.exception.NotificationException;
import com.example.Veco.domain.notification.exception.code.NotiErrorCode;

public enum FilterType {
    PRIORITY, GOAL, STATE, EXTERNAL;

    public static FilterType from(String filter) {

        if (filter == null) return STATE;

        try {
            return FilterType.valueOf(filter.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotificationException(NotiErrorCode.QUERY_INVALID);
        }

    }
}