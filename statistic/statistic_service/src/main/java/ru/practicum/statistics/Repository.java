package ru.practicum.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statistics.model.Log;
import ru.practicum.statistics.model.LogResp;

import java.time.LocalDateTime;
import java.util.List;

public interface Repository extends JpaRepository<Log, Long> {

    @Query("select new ru.practicum.statistics.model.LogResp(l.app, l.uri, count(l.ip)) "
            + " from Log l "
            + " where l.timestamp between ?1 and ?2 "
            + " group by l.app, l.uri "
            + " order by count(l.ip) desc")
    List<LogResp> findAllStatisticWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statistics.model.LogResp(l.app, l.uri, count(distinct l.ip)) "
            + " from Log l "
            + " where l.timestamp between ?1 and ?2 "
            + " group by l.app, l.uri"
            + " order by count(distinct l.ip) desc")
    List<LogResp> findAllUniqueStatisticsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statistics.model.LogResp(l.app, l.uri, count(l.ip)) "
            + " from Log l "
            + " where l.uri in (?3) and l.timestamp between ?1 and ?2 "
            + " group by l.app, l.uri "
            + " order by count(l.ip) desc")
    List<LogResp> findAllStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.statistics.model.LogResp(l.app, l.uri, count(distinct l.ip)) "
            + " from Log l "
            + " where l.uri in (?3) and l.timestamp between ?1 and ?2 "
            + " group by l.app, l.uri"
            + " order by count(distinct l.ip) desc")
    List<LogResp> findAllUniqueStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);
}
