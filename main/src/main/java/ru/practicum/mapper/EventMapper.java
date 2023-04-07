package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event makeEvent(NewEventDto dto) {
        return (Event) makeEventObject(new Event(), dto);
    }

    public static EventFullDto makeEventFullDto(Event event) {
        return (EventFullDto) makeEventObject(new EventFullDto(), event);
    }

    public static EventShortDto makeShortDto(Event event) {
        return (EventShortDto) makeEventObject(new EventShortDto(), event);
    }

    public static Event makeEventFromUpdateEventUserRequest(Event event, UpdateEventUserRequest dto) {
        return (Event) makeEventObject(event, dto);
    }

    public static Event makeEventFromUpdateEventAdminRequest(Event event, UpdateEventAdminRequest dto) {
        return (Event) makeEventObject(event, dto);
    }

    @SneakyThrows
    private static Object makeEventObject(Object to, Object from) {
        Field[] fieldsTo = to.getClass().getDeclaredFields();
        Field[] fieldsFrom = from.getClass().getDeclaredFields();
        for (Field fieldTo : fieldsTo) {
            for (Field fieldFrom : fieldsFrom) {
                if (fieldTo.getName().equals(fieldFrom.getName()) && fieldTo.getType() == fieldFrom.getType()) {
                    String setter = "set" + fieldTo.getName().substring(0, 1).toUpperCase() + fieldTo.getName().substring(1);
                    String getter = "get" + fieldTo.getName().substring(0, 1).toUpperCase() + fieldTo.getName().substring(1);
                    Method setterMethod = to.getClass().getMethod(setter, fieldTo.getType());
                    Method getterMethod = from.getClass().getMethod(getter);
                    Object filed = getterMethod.invoke(from);
                    if (filed != null) {
                        setterMethod.invoke(to, filed);
                    }
                    break;
                }
                if (fieldTo.getName().equals(fieldFrom.getName()) && fieldFrom.getType().equals(LocalDateTime.class)) {
                    String setter = "set" + fieldTo.getName().substring(0, 1).toUpperCase() + fieldTo.getName().substring(1);
                    String getter = "get" + fieldTo.getName().substring(0, 1).toUpperCase() + fieldTo.getName().substring(1);
                    Method getterMethod = from.getClass().getMethod(getter);
                    Method setterMethod = to.getClass().getMethod(setter, String.class);
                    Object field = getterMethod.invoke(from);
                    if (field != null && field.getClass().equals(LocalDateTime.class)) {
                        LocalDateTime time = LocalDateTime.parse(field.toString());
                        setterMethod.invoke(to, time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                }
            }
        }
        return to;
    }
}
