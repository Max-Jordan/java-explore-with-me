package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.mapper.CategoryMapper.makeCategoryDto;
import static ru.practicum.mapper.UserMapper.makeUserShortDto;

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
                String setter = "set" + fieldTo.getName().substring(0, 1).toUpperCase() + fieldTo.getName().substring(1);
                String getter = "get" + fieldTo.getName().substring(0, 1).toUpperCase() + fieldTo.getName().substring(1);
                if (fieldTo.getName().equals(fieldFrom.getName()) && fieldTo.getType() == fieldFrom.getType()) {
                    Method setterMethod = to.getClass().getMethod(setter, fieldTo.getType());
                    Method getterMethod = from.getClass().getMethod(getter);
                    Object filed = getterMethod.invoke(from);
                    if (filed != null) {
                        setterMethod.invoke(to, filed);
                    }
                    break;
                }
                if (fieldTo.getName().equals(fieldFrom.getName()) && fieldFrom.getType().equals(LocalDateTime.class)) {
                    Method getterMethod = from.getClass().getMethod(getter);
                    Method setterMethod = to.getClass().getMethod(setter, String.class);
                    Object field = getterMethod.invoke(from);
                    if (field != null && field.getClass().equals(LocalDateTime.class)) {
                        LocalDateTime time = LocalDateTime.parse(field.toString());
                        setterMethod.invoke(to, time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    break;
                }
                if (fieldTo.getName().equals(fieldFrom.getName()) && String.valueOf(fieldTo.getType()).endsWith("Dto")) {
                    Method getterMethod = from.getClass().getMethod(getter);
                    Object field = getterMethod.invoke(from);
                    if(field.getClass().equals(Category.class)) {
                        to.getClass().getMethod(setter, CategoryDto.class).invoke(to, makeCategoryDto((Category) field));
                    }
                    if(field.getClass().equals(User.class)) {
                        to.getClass().getMethod(setter, UserShortDto.class).invoke(to, makeUserShortDto((User) field));

                    }
                }
            }
        }
        return to;
    }
}
