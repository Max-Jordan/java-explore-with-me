package ru.practicum.event;

import ru.practicum.event.dto.*;
import ru.practicum.event.dto.enums.SortEvent;
import ru.practicum.event.dto.enums.StatusEvent;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

    List<EventFullDto> getEventsWithParametersByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd, boolean onlyAvailable, SortEvent sort, Integer from,
                                                     Integer size, HttpServletRequest request);

    List<EventFullDto> getEventsWithParametersByAdmin(List<Long> users, List<StatusEvent> states, List<Long> categoriesId,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto);

    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    Event getEventById(Long id);
}
