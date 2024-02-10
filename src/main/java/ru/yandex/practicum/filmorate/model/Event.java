package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
        LIKE("film_id"), REVIEW("review_id"), FRIEND("friend_id");
        private String columnName;

        EventType(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public enum OperationType {
        REMOVE, ADD, UPDATE
    }
}
