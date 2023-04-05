package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.enums.StatusEvent;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mapper.RequestMapper.makeRequestDto;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto saveRequest(Long userId, Long eventId) {
        Request request = new Request();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event was not found"));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RequestException("Initiator of event can't be requester");
        }
        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            throw new RequestException("Limit of request");
        }
        if (event.getState() != StatusEvent.PUBLISHED) {
            throw new RequestException("Event not published");
        }
        if (Boolean.FALSE.equals(event.getRequestModeration())) {
            request.setStatus(StatusRequest.CONFIRMED);
        }
        request.setStatus(StatusRequest.PENDING);
        User user = userService.getUserById(userId);
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        return makeRequestDto(repository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        return repository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = repository.findRequestByIdAndRequesterId(requestId, userId);
        request.setStatus(StatusRequest.CANCELED);
        return makeRequestDto(repository.saveAndFlush(request));
    }

    @Override
    public List<RequestDto> findAllMemberRequests(Long userId, Long eventId) {
        return repository.findAllByEventIdAndRequesterId(eventId, userId).stream()
                .map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> findAllByRequestsId(List<Long> requestsId) {
        return repository.findAllRequestByIdIn(requestsId).stream()
                .map(RequestMapper::makeRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRequest(Long idRequest, StatusRequest status) {
        Request request = repository.findById(idRequest).orElseThrow(() -> new NotFoundException("Request not found"));
        request.setStatus(status);
        repository.save(request);
    }
}
