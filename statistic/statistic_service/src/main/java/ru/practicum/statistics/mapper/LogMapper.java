package ru.practicum.statistics.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.statistics.model.EndpointHit;
import ru.practicum.statistics.model.ViewStat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogMapper {

    public static EndpointHit makeLog(RequestStatDto dto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(dto.getApp());
        endpointHit.setIp(dto.getIp());
        endpointHit.setUri(dto.getUri());
        endpointHit.setTimestamp(dto.getTimestamp());
        return endpointHit;
    }

    public static ResponseStatDto makeResponseDto(ViewStat stat) {
        ResponseStatDto dto = new ResponseStatDto();
        dto.setApp(stat.getApp());
        dto.setHits(stat.getHits());
        dto.setUri(stat.getUri());
        return dto;
    }

    public static RequestStatDto makeRequestDto(EndpointHit endpointHit) {
        RequestStatDto dto = new RequestStatDto();
        dto.setApp(endpointHit.getApp());
        dto.setUri(endpointHit.getUri());
        dto.setIp(endpointHit.getIp());
        dto.setTimestamp(endpointHit.getTimestamp());
        dto.setId(endpointHit.getId());
        return dto;
    }
}
