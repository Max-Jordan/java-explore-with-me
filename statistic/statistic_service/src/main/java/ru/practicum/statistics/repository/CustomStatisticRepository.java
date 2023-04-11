package ru.practicum.statistics.repository;

import ru.practicum.statistics.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomStatisticRepository {

    List<ViewStat> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
