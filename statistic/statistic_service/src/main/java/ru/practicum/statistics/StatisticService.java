package ru.practicum.statistics;

import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    void save(RequestStatDto dto);

    List<ResponseStatDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
