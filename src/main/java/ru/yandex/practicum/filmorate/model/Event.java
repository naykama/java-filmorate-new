package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class Event {
    private int eventId;
    @NotBlank
    private final int userId;
    @NotBlank
    private final int entityId;
    @NotNull
    private final EventType eventType;
    @NotNull
    private final OperationType operation;
    long timestamp = System.currentTimeMillis();

    public enum EventType {LIKE, REVIEW, FRIEND}
    public enum OperationType {REMOVE, ADD, UPDATE}
}
