package ru.practicum.statistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statistics.model.EndpointHit;
import ru.practicum.statistics.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long>, CustomStatisticRepository {

//    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(l.ip))"
//            + " from EndpointHit l "
//            + " where l.timestamp between :start and :end"
//            + " group by l.app, l.uri")
//    List<ViewStat> findAllStatisticWithoutUris(LocalDateTime start, LocalDateTime end);
//
//    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip))"
//            + " from EndpointHit l "
//            + " where l.timestamp between :start and :end "
//            + " group by l.app, l.uri")
//    List<ViewStat> findAllUniqueStatisticsWithoutUris(LocalDateTime start, LocalDateTime end);

//    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(l.ip))"
//            + " from EndpointHit l"
//            + " where l.timestamp between :start and :end and l.uri in (:uris)"
//            + " group by l.app, l.uri"
//            + " order by count(l.ip) desc")
//    List<ViewStat> findAllStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);

//    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip))"
//            + " from EndpointHit l"
//            + " where l.timestamp between :start and :end and l.uri in (:uris)"
//            + " group by l.app, l.uri"
//            + " order by count(distinct l.ip) desc")
//    List<ViewStat> findAllUniqueStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.statistics.model.ViewStat(l.app, l.uri, count(distinct l.ip))" +
            " from EndpointHit l" +
            " where l.timestamp between :start and :end" +
            " group by l.app, l.uri")
    List<ViewStat> test(LocalDateTime start, LocalDateTime end);
}
