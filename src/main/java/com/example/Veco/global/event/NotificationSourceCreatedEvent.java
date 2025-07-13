package com.example.Veco.global.event;

import com.example.Veco.global.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationSourceCreatedEvent {

    private final Long teamId;
    private final Category type;
    private final Long typeId;

}