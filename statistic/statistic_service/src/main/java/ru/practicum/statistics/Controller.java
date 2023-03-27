package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    private final Service service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody RequestStatDto dto) {
        log.info("Received a request to save statistics {}", dto);
        service.save(dto);
    }

    @GetMapping("/stats")
    public List<ResponseStatDto> getStat(@RequestParam("start") LocalDateTime start,
                                         @RequestParam("end") LocalDateTime end,
                                         @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                                         @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Received a request to get statistic with parameters: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return service.getStatistics(start, end, uris, unique);
    }
}
