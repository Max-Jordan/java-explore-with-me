package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.enums.SortEvent;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventController {

    private final EventService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@Positive @PathVariable Long id, HttpServletRequest request) {
        log.info("Received a request to view the event {} without authorization", id);
        return service.getEvent(id, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsWithParametersByUser(@RequestParam(name = "text", required = false) String text,
                                                            @RequestParam(name = "categories",
                                                            required = false) List<Long> categories,
                                                            @RequestParam(name = "paid", required = false) Boolean paid,
                                                            @RequestParam(name = "rangeStart",
                                                            required = false) LocalDateTime rangeStart,
                                                            @RequestParam(name = "rangeEnd",
                                                            required = false) LocalDateTime rangeEnd,
                                                            @RequestParam(name = "onlyAvailable",
                                                            required = false,
                                                            defaultValue = "false") boolean onlyAvailable,
                                                            @RequestParam(name = "sort", required = false) SortEvent sort,
                                                            @RequestParam(name = "from",
                                                            required = false, defaultValue = "0") Integer from,
                                                            @RequestParam(name = "size",
                                                            required = false, defaultValue = "10") Integer size,
                                                            HttpServletRequest request) {
        log.info("Received a request to view events without authorization with parameters: text {}, categories {}," +
                        " paid {}, rangeStart {}, rangeEnd {}, onlyAvailable {}, sort {}, from {}, size {} ",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return service.getEventsWithParametersByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

}
