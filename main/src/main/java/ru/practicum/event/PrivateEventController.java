package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.RequestDto;
import ru.practicum.request.StatusRequestUpdateResponse;
import ru.practicum.request.UpdateRequestStatusDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService service;

    @PostMapping
    public EventDto createEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return service.saveEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                               @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return service.getEventsByInitiatorId(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventByUser(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @Valid @RequestBody UpdateEventUserRequest dto) {
        return service.updateEventByUser(userId, eventId, dto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEventByUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public StatusRequestUpdateResponse updateStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                    @RequestBody UpdateRequestStatusDto dto) {
        return service.updateRequests(userId, eventId, dto);
    }
}
