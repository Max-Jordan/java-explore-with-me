package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
public class StatisticController {

    private final StatisticService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestStatDto add(@Valid @RequestBody RequestStatDto dto) {
        log.info("Received a request to save statistics {}", dto);
        return service.save(dto);
    }

    @GetMapping("/stats")
    public List<ResponseStatDto> getStat(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                         @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                                         @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Received a request to get statistic with parameters: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return service.getStatistics(start, end, uris, unique);
    }
}
