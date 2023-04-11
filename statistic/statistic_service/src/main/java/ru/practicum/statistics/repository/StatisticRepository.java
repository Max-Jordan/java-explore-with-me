package ru.practicum.statistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.statistics.model.EndpointHit;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long>, CustomStatisticRepository {
}
