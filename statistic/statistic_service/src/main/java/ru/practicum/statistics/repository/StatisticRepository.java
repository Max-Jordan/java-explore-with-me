package ru.practicum.statistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statistics.model.EndpointHit;
import ru.practicum.statistics.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long>, CustomStatisticRepository {
}
