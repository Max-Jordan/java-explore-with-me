package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.enums.StatusEvent;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerForAdmin {

    private final EventService service;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                @Valid @RequestBody UpdateEventAdminRequest dto) {
        log.info("Received a request to update event with id {} from admin", eventId);
        return service.updateEvent(eventId, dto);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                    @RequestParam(name = "states", required = false) StatusEvent states,
                                    @RequestParam(name = "categories", required = false) List<Long> categoriesId,
                                    @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                    @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                    @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        log.info("Received a request to get events with parameters: users {}, states{}, categories {}, rangeStart {}," +
                " rangeEnd {}, from {}, size {} from admin", users, states, categoriesId, rangeStart, rangeEnd, from, size);
        return service.getEventsWithParametersByAdmin(users, states, categoriesId, rangeStart, rangeEnd, from, size);
    }
}
