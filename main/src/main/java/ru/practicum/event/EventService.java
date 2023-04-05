package ru.practicum.event;

import ru.practicum.event.enums.SortEvent;
import ru.practicum.event.enums.StatusEvent;
import ru.practicum.event.model.Event;
import ru.practicum.request.RequestDto;
import ru.practicum.request.StatusRequestUpdateResponse;
import ru.practicum.request.UpdateRequestStatusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventDto getEventById(Long id);

    List<EventDto> getEventsWithParametersByUser(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                 String rangeEnd, boolean onlyAvailable, SortEvent sort, Integer from,
                                                 Integer size, HttpServletRequest request);

    List<EventDto> getEventsWithParametersByAdmin(List<Long> users, StatusEvent states, List<Long> categoriesId,
                                                  String rangeStart, String rangeEnd, Integer from, Integer size);

    EventDto updateEvent(Long eventId, UpdateEventAdminRequest dto);

    EventDto saveEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByInitiatorId(Long userId, Integer from, Integer size);

    EventDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventDto getEventByUser(Long userId, Long eventId);

    StatusRequestUpdateResponse updateRequests(Long userId, Long eventId, UpdateRequestStatusDto dto);

    List<RequestDto> getRequests(Long userId, Long eventId);

    Event getEvent(Long eventId);
}
