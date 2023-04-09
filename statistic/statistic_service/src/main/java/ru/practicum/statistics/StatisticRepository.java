package ru.practicum.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.statistics.model.EndpointHit;
import ru.practicum.statistics.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip)) "
            + " from EndpointHit l "
            + " where l.timestamp between :start and :end "
            + " group by l.app, l.uri, l.id "
            + " order by count(l.ip) desc")
    List<ViewStat> findAllStatisticWithoutUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip))"
            + " from EndpointHit l "
            + " where l.timestamp between :start and :end "
            + " group by l.app, l.uri, l.id"
            + " order by count(distinct l.ip) desc")
    List<ViewStat> findAllUniqueStatisticsWithoutUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip)) "
            + " from EndpointHit l "
            + " where l.timestamp between :start and :end and l.uri in (:uris) "
            + " group by l.app, l.uri, l.id "
            + " order by count(l.ip) desc")
    List<ViewStat> findAllStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);

    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip)) "
            + " from EndpointHit l "
            + " where l.uri in (:uris) and l.timestamp between :start and :end "
            + " group by l.app, l.uri, l.id"
            + " order by count(distinct l.ip) desc")
    List<ViewStat> findAllUniqueStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                           @Param("uris") List<String> uris);

}
