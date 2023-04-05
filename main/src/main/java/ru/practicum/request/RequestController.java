package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;

    @PostMapping
    public RequestDto saveRequest(@Positive @PathVariable Long userId, @Positive @RequestParam Long eventId) {
        log.info("Received a request to add request from " + userId + " to event " + eventId);
        return service.saveRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getRequests(@Positive @PathVariable Long userId) {
        log.info("Received a request to get requests from the user " + userId);
        return service.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@Positive @PathVariable Long userId, @Positive@PathVariable Long requestId) {
        log.info("Received a requests to cancel request with id " + requestId + " from user " + userId);
        return service.cancelRequest(userId, requestId);
    }
}
