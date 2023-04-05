package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.StatisticClient;
import ru.practicum.category.CategoryService;
import ru.practicum.category.model.Category;
import ru.practicum.constants.DatePattern;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.StatusRequest;
import ru.practicum.request.dto.StatusRequestUpdateResponse;
import ru.practicum.request.dto.UpdateRequestStatusDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.dto.enums.SortEvent;
import ru.practicum.event.dto.enums.StatusEvent;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestException;
import ru.practicum.exception.TimeException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.request.*;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private final EntityManager em;
    private CriteriaBuilder criteriaBuilder;
    private static final String EVENT_DATE = "eventDate";
    private static final String TIME_MESSAGE = "The event cannot start earlier than " + LocalTime.now().plusHours(2);

    @Override
    public EventDto getEventById(Long id) {
        return makeEventDto(repository.findById(id).orElseThrow(() -> new NotFoundException("Event with the id " +
                id + " was not found")));
    }

    @Override
    public List<EventDto> getEventsWithParametersByUser(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                        String rangeEnd, boolean onlyAvailable, SortEvent sort, Integer from,
                                                        Integer size, HttpServletRequest request) {
        criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.hasText(text)) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%"), criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%")));
        }
        if (!categories.isEmpty()) {
            predicates.add(root.get("category").in(categories));
        }
        if (paid != null) {
            if (paid) {
                predicates.add(criteriaBuilder.isTrue(root.get("paid")));
            } else {
                predicates.add(criteriaBuilder.isFalse(root.get("paid")));
            }
        }
        if (StringUtils.hasText(rangeStart)) {
            LocalDateTime startEvent = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(DatePattern.DATE_FORMAT));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(EVENT_DATE).as(LocalDateTime.class), startEvent));
        }
        if (StringUtils.hasText(rangeEnd)) {
            LocalDateTime endEvent = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(DatePattern.DATE_FORMAT));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(EVENT_DATE).as(LocalDateTime.class), endEvent));
        }
        query.select(root);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Event> typedQuery = em.createQuery(query);
        Page<Event> page = new PageImpl<>(typedQuery.getResultList(), makePageable(from, size), size);
        if (onlyAvailable) {
            return page.stream().filter(e -> e.getConfirmedRequests() < e.getParticipantLimit())
                    .map(EventMapper::makeEventDto).collect(Collectors.toList());
        }
        if (sort != null) {
            if (sort.equals(SortEvent.EVENT_DATE)) {
                return page.stream().sorted(Comparator.comparing(Event::getEventDate))
                        .map(EventMapper::makeEventDto
                        ).collect(Collectors.toList());
            } else {
                return page.stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(EventMapper::makeEventDto
                        ).collect(Collectors.toList());

            }
        }
        sendStatistic(request);
        setView(page.getContent());
        return page.stream().map(EventMapper::makeEventDto).collect(Collectors.toList());
    }

    @Override
    public List<EventDto> getEventsWithParametersByAdmin(List<Long> users, StatusEvent states, List<Long> categoriesId,
                                                         String rangeStart, String rangeEnd, Integer from, Integer size) {
        em.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);
        query.select(root);
        List<Predicate> predicates = new ArrayList<>();
        if (!users.isEmpty()) {
            predicates.add(root.get("initiator").in(users));
        }
        if (states != null) {
            predicates.add(root.get("state").in(states));
        }
        if (!categoriesId.isEmpty()) {
            predicates.add(root.get("category").in(states));
        }
        if (rangeStart != null && rangeEnd != null) {
            predicates.add(criteriaBuilder.between(root.get(EVENT_DATE), rangeStart, rangeEnd));
        }
        query.select(root);
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Event> typedQuery = em.createQuery(query);
        Page<Event> page = new PageImpl<>(typedQuery.getResultList(), makePageable(from, size), size);
        setView(page.getContent());
        return page.stream().map(EventMapper::makeEventDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeException(TIME_MESSAGE);
        }
        Event event = makeEventFromUpdateEventAdminRequest(getEvent(eventId), dto);
        if (dto.getCategory() != null) {
            event.setCategory(makeCategory(categoryService.getCategory(dto.getCategory())));
        }
        return makeEventDto(event);
    }

    @Override
    @Transactional
    public EventDto saveEvent(Long userId, NewEventDto dto) {
        Category category = makeCategory(categoryService.getCategory(dto.getCategory()));
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeException(TIME_MESSAGE);
        }
        Event event = makeEvent(dto);
        event.setCategory(category);
        event.setInitiator(userService.getUserById(userId));
        return makeEventDto(repository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByInitiatorId(Long userId, Integer from, Integer size) {
        return repository.findAllByInitiatorId(userId, makePageable(from, size)).stream()
                .map(EventMapper::makeShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new TimeException(TIME_MESSAGE);
        }
        Event event = makeEventFromUpdateEventUserRequest(repository.findByIdAndInitiatorId(eventId, userId), dto);
        if (dto.getCategory() != null) {
            event.setCategory(makeCategory(categoryService.getCategory(dto.getCategory())));
        }

        return makeEventDto(event);
    }

    @Override
    public EventDto getEventByUser(Long userId, Long eventId) {
        return makeEventDto(repository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public StatusRequestUpdateResponse updateRequests(Long userId, Long eventId, UpdateRequestStatusDto dto) {
        Event event = repository.findById(eventId).orElseThrow(() -> new NotFoundException("Event was not found"));
        StatusRequestUpdateResponse result = new StatusRequestUpdateResponse();
        if (event.getParticipantLimit() == 0 || Boolean.FALSE.equals(event.getRequestModeration())) {
            return result;
        }
        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new RequestException("Request limit reached");
        }
        List<RequestDto> requests = requestService.findAllByRequestsId(dto.getRequestIds());
        for (RequestDto request : requests) {
            if (request.getStatus() != StatusRequest.PENDING) {
                throw new RequestException("The request has no status PENDING ");
            }
            if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests()) && dto.getStatus() == StatusRequest.CONFIRMED) {
                request.setStatus(StatusRequest.CONFIRMED);
                result.getConfirmedRequests().add(request);
                requestService.updateRequest(request.getId(), StatusRequest.CONFIRMED);
            } else {
                request.setStatus(StatusRequest.REJECTED);
                result.getRejectedRequests().add(request);
            }

        }
        return result;
    }

    @Override
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        return requestService.findAllMemberRequests(userId, eventId);
    }

    @Override
    public Event getEvent(Long eventId) {
        return repository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id " + eventId +
                " doesn't exist"));
    }

    private void sendStatistic(HttpServletRequest request) {
        RequestStatDto statDto = new RequestStatDto();
        statDto.setApp("main");
        statDto.setIp(request.getRemoteAddr());
        statDto.setUri(request.getRequestURI());
        statDto.setTimestamp(LocalDateTime.now());
        client.save(statDto);
    }

    private void setView(List<Event> events) {
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

        List<ResponseStatDto> stats = client.getStatistics(start, LocalDateTime.now(), uris, false).getBody();
        if (stats != null) {
            stats.forEach(stat ->
                    eventsUri.get(stat.getUri()).setViews(stat.getHits()));
        }
    }
}
