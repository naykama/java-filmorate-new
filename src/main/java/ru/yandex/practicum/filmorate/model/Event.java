package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Event {
    private int eventId;
    @NotNull
    private final int userId;
    @NotNull
    private final int entityId;
    @NotNull
    private final EventType eventType;
    @NotNull
    private final OperationType operation;
    private long timestamp = System.currentTimeMillis();

    public enum EventType {
        LIKE, REVIEW, FRIEND
    }

    public enum OperationType {
        REMOVE, ADD, UPDATE
    }
}
