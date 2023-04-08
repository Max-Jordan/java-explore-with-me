package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticClient;
import ru.practicum.category.CategoryService;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.dto.enums.SortEvent;
import ru.practicum.event.dto.enums.StateActionForUser;
import ru.practicum.event.dto.enums.StatusEvent;
import ru.practicum.event.model.Event;
import ru.practicum.exception.EventException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.TimeException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.StatusRequest;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CategoryMapper.makeCategory;
import static ru.practicum.mapper.EventMapper.*;
import static ru.practicum.mapper.PaginationMapper.makePageable;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatisticClient client;
    private final EventRepository repository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final RequestService requestService;
    private static final String TIME_MESSAGE = "The event cannot start earlier than " + LocalTime.now().plusHours(2);
    private static final String INITIATOR_EXCEPTION = "The event id does not belong to the user id";

    @Override
    public Event getEventById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Event with the id " +
                id + " was not found"));
    }

    @Override
    public List<EventFullDto> getEventsWithParametersByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                            LocalDateTime rangeEnd, boolean onlyAvailable, SortEvent sort, Integer from,
                                                            Integer size, HttpServletRequest request) {
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        rangeEnd = rangeEnd == null ? LocalDateTime.now().plusHours(2) : rangeEnd;
        List<Event> events = onlyAvailable ?
                repository.findEventsByAvailable(text, paid, rangeStart, rangeEnd, categories, makePageable(from, size))
                        .stream()
                        .filter(event -> event.getParticipantLimit() > requestService.findAllRequests(event.getId()).size())
                        .collect(Collectors.toList()) :
                repository.findAllEvent(text, paid, rangeStart, rangeEnd, categories, makePageable(from, size)).toList();
        if (sort != null) {
            if (sort.equals(SortEvent.EVENT_DATE)) {
                events = events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
            }
            if (sort.equals(SortEvent.VIEWS)) {
                events = events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
            }
        }
        return events.stream()
                .map(EventMapper::makeEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsWithParametersByAdmin(List<Long> users, List<StatusEvent> states, List<Long> categoriesId,
                                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Page<Event> page = repository.findEventByInitiatorIdAndStateAndCategory_IdAndEventDateBetween(users, states, categoriesId,
                rangeStart, rangeEnd, makePageable(from, size));
        if (page.hasContent()) {
            setViewForList(page.getContent());
        }
        return page.stream().map(EventMapper::makeEventFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeException(TIME_MESSAGE);
        }
        Event event = makeEventFromUpdateEventAdminRequest(getEventById(eventId), dto);
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != StatusEvent.PENDING) {
                        throw new EventException("Event cannot be published");
                    }
                    event.setState(StatusEvent.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    if (event.getState() == StatusEvent.PUBLISHED) {
                        throw new EventException("It is not possible to cancel the event");
                    }
                    event.setState(StatusEvent.CANCELED);
                    break;
            }
        }
        if (dto.getCategory() != null) {
            event.setCategory(makeCategory(categoryService.getCategory(dto.getCategory())));
        }
        return makeEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto saveEvent(Long userId, NewEventDto dto) {
        Category category = makeCategory(categoryService.getCategory(dto.getCategory()));
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeException(TIME_MESSAGE);
        }
        Event event = makeEvent(dto);
        event.setCreatedOn(LocalDateTime.now());
        event.setCategory(category);
        event.setInitiator(userService.getUserById(userId));
        event.setState(StatusEvent.PENDING);
        return makeEventFullDto(repository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByInitiatorId(Long userId, Integer from, Integer size) {
        return repository.findAllByInitiatorId(userId, makePageable(from, size)).stream()
                .map(EventMapper::makeShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeException(TIME_MESSAGE);
        }
        Event event = makeEventFromUpdateEventUserRequest(repository.findByIdAndInitiatorId(eventId, userId), dto);
        if (!event.getInitiator().getId().equals(checkUser(userId).getId())) {
            throw new EventException(INITIATOR_EXCEPTION);
        }
        if (event.getState().equals(StatusEvent.PUBLISHED)) {
            throw new EventException("It is not possible to change the published event");
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW)) {
                event.setState(StatusEvent.PENDING);
            }
            if (dto.getStateAction().equals(StateActionForUser.CANCEL_REVIEW)) {
                event.setState(StatusEvent.CANCELED);
            }
        }
        if (dto.getCategory() != null) {
            event.setCategory(makeCategory(categoryService.getCategory(dto.getCategory())));
        }

        return makeEventFullDto(event);
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        return makeEventFullDto(repository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        List<ParticipationRequestDto> confirmedRequests = requestService.findAllRequests(eventId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EventException(INITIATOR_EXCEPTION);
        }

        if (event.getParticipantLimit() == confirmedRequests.size()) {
            throw new EventException("The limit of confirmed requests has been reached");
        }

        List<ParticipationRequestDto> requests = requestService.findAllByRequestsId(dto.getRequestIds());
        if (dto.getStatus().equals(StatusRequest.REJECTED)) {
            rejected = requests.stream()
                    .peek(request -> request.setStatus(StatusRequest.REJECTED))
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmed, rejected);
        }
        if (event.getParticipantLimit() == 0 || Boolean.TRUE.equals(!event.getRequestModeration())) {
            confirmed = requests.stream()
                    .peek(request -> request.setStatus(dto.getStatus()))
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmed, rejected);
        }

        for (ParticipationRequestDto request : requests) {
            if (request.getStatus().equals((StatusRequest.PENDING))) {
                if (event.getParticipantLimit() != confirmedRequests.size()) {
                    request.setStatus(StatusRequest.CONFIRMED);
                    confirmed.add(request);
                    event.setConfirmedRequests(requestService.findRequestByStatus(StatusRequest.CONFIRMED));
                } else {
                    request.setStatus(StatusRequest.REJECTED);
                    rejected.add(request);
                }
            }
        }
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        User user = checkUser(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new EventException(INITIATOR_EXCEPTION);
        }
        return requestService.findAllRequests(eventId);
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        sendStatistic(request);
        Event event = repository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id " + eventId +
                " doesn't exist"));
        setView(event);
        return makeEventFullDto(event);
    }

    private void sendStatistic(HttpServletRequest request) {
        RequestStatDto statDto = new RequestStatDto();
        statDto.setApp("main");
        statDto.setIp(request.getRemoteAddr());
        statDto.setUri(request.getRequestURI());
        statDto.setTimestamp(LocalDateTime.now());
        client.save(statDto);
    }

    private void setViewForList(List<Event> events) {
        LocalDateTime start = events.get(0).getCreatedOn();
        List<String> uris = new ArrayList<>();
        Map<String, Event> eventsUri = new HashMap<>();
        String uri = "";
        for (Event event : events) {
            if (start.isBefore(event.getCreatedOn())) {
                start = event.getCreatedOn();
            }
            uri = "/events/" + event.getId();
            uris.add(uri);
            eventsUri.put(uri, event);
            event.setViews(0L);
        }

        List<ResponseStatDto> stats = getStatistic(start, LocalDateTime.now(), uris);
        if (stats != null) {
            stats.forEach(stat ->
                    eventsUri.get(stat.getUri()).setViews(stat.getHits()));
        }
    }

    private void setView(Event event) {
        List<String> uris = List.of("/events/" + event.getId());

        List<ResponseStatDto> stats = getStatistic(event.getCreatedOn(), LocalDateTime.now(), uris);
        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else {
            event.setViews(0L);
        }
    }

    private List<ResponseStatDto> getStatistic(LocalDateTime startTime, LocalDateTime endTime, List<String> uris) {
        return client.getStatistics(startTime, endTime, uris, false).getBody() == null ?
                Collections.emptyList() :
                client.getStatistics(startTime, endTime, uris, false).getBody();
    }

    private User checkUser(Long userId) {
        return userService.getUserById(userId);
    }
}
