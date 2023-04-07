package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@PositiveOrZero @PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(name = "eventId") Long eventId) {
        log.info("Received a request to add request from " + userId + " to event " + eventId);
        return service.saveRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PositiveOrZero @PathVariable Long userId) {
        log.info("Received a request to get requests from the user " + userId);
        return service.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PositiveOrZero @PathVariable Long userId,
                                                 @PositiveOrZero @PathVariable Long requestId) {
        log.info("Received a requests to cancel request with id " + requestId + " from user " + userId);
        return service.cancelRequest(userId, requestId);
    }
}
