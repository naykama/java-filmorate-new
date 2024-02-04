package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Event.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class EventDbStorageImpl implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("events")
                                            .usingGeneratedKeyColumns("id");
        Map<String, Object> params = Map.of("user_id", event.getUserId(), "operation_type",
                                            event.getOperation().toString(), getColumnNameByEventType(event.getEventType()),
                                            event.getEntityId());
        Number eventId = simpleJdbcInsert.executeAndReturnKey(params);
        event.setEventId(eventId.intValue());
    }

    @Override
    public List<Event> getEventsForUserByID(int userId) {
        String sql = "SELECT * FROM events WHERE user_id = ?";
        return jdbcTemplate.query(sql, eventRowMapper(), userId);
    }

    private String getColumnNameByEventType(EventType eventType) {
        switch (eventType) {
            case LIKE:
                return "film_id";
            case FRIEND:
                return "friend_id";
            case REVIEW:
                return "review_id";
            default:
                throw new EntityNotFoundException(String.format("Тип события: %s не обрабатывается", eventType));
        }
    }

    private RowMapper<Event> eventRowMapper() {
        return (rs, rowNum) -> {
            String ColumnNameWithEntityId = getNotNullEntityColumnName(rs);
            Event event = new Event(rs.getInt("user_id"), rs.getInt(ColumnNameWithEntityId),
                    getEventTypeByColumnName(ColumnNameWithEntityId),
                    Event.OperationType.valueOf(rs.getString("operation_type")));
            event.setEventId(rs.getInt("id"));
            return event;
        };
    }

    private String getNotNullEntityColumnName(ResultSet rs) throws SQLException {
        return rs.getString("film_id") != null ? "film_id"
                : rs.getString("friend_id") != null ? "friend_id" : "review_id";
    }

    private EventType getEventTypeByColumnName(String columnName) {
        return columnName.equals("film_id") ? EventType.LIKE : columnName.equals("friend_id") ? EventType.FRIEND :
                EventType.REVIEW;
    }
}
