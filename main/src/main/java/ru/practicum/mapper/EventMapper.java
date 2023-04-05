package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.practicum.event.*;
import ru.practicum.event.model.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static Event makeEvent(NewEventDto dto) {
        return (Event) makeEventObject(new Event(), dto);
    }

    public static EventDto makeEventDto(Event event) {
        return (EventDto) makeEventObject(new EventDto(), event);
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
        Field[] fields = to.getClass().getDeclaredFields();
        Field[] fieldsSecond = from.getClass().getDeclaredFields();
        for (Field f : fields) {
            for (Field s : fieldsSecond) {
                if (f.getName().equals(s.getName()) && f.getType() == s.getType()) {
                    String setter = "set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
                    String getter = "get" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
                    Method setterMethod = to.getClass().getMethod(setter, f.getType());
                    Method getterMethod = from.getClass().getMethod(getter);
                    Object filed = getterMethod.invoke(from);
                    if (filed != null) {
                        setterMethod.invoke(to, filed);
                    }
                }
            }
        }
        return to;
    }
}
