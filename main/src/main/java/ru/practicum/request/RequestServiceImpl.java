package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.enums.StatusEvent;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.StatusRequest;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mapper.RequestMapper.makeParticipationRequestDto;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        Request request = new Request();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("The event doesn't exist"));
        if (Boolean.TRUE.equals(repository.existsByRequesterIdAndEventId(userId, eventId))) {
            throw new RequestException("The request already exists");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new RequestException("Initiator of event can't be requester");
        }
        if (repository.findByEventId(eventId).size() == event.getParticipantLimit()) {
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
        return makeParticipationRequestDto(repository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        return repository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::makeParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = repository.findRequestByIdAndRequesterId(requestId, userId);
        request.setStatus(StatusRequest.CANCELED);
        return makeParticipationRequestDto(repository.saveAndFlush(request));
    }

    @Override
    public List<ParticipationRequestDto> findAllRequests(Long eventId) {
        return repository.findByEventId(eventId).stream()
                .map(RequestMapper::makeParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> findAllByRequestsId(List<Long> requestsId) {
        return repository.findAllRequestByIdIn(requestsId).stream()
                .map(RequestMapper::makeParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateRequest(Long idRequest, StatusRequest status) {
        Request request = repository.findById(idRequest).orElseThrow(() -> new NotFoundException("Request not found"));
        request.setStatus(status);
        repository.save(request);
    }

    @Override
    public Integer findRequestByStatus(StatusRequest status) {
        return repository.countAllByStatus(status);
    }
}
