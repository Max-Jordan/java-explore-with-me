package ru.practicum.statistics.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.statistics.model.Log;
import ru.practicum.statistics.model.ViewStat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMapper {

    public static Log makeLog(RequestStatDto dto) {
        Log log = new Log();
        log.setApp(dto.getApp());
        log.setIp(dto.getIp());
        log.setUri(dto.getUri());
        log.setTimestamp(dto.getTimestamp());
        return log;
    }

    public static ResponseStatDto makeResponseDto(ViewStat log) {
        ResponseStatDto dto = new ResponseStatDto();
        dto.setApp(log.getApp());
        dto.setHits(log.getHits());
        dto.setUri(log.getUri());
        return dto;
    }
}
