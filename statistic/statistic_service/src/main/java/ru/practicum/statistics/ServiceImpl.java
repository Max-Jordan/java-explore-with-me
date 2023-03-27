package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.statistics.mapper.LogMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.statistics.mapper.LogMapper.makeLog;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceImpl implements Service {

    private final Repository rep;

    @Override
    @Transactional
    public void save(RequestStatDto dto) {
        rep.save(makeLog(dto));
    }

    @Override
    public List<ResponseStatDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (uris.isEmpty()) {
                return rep.findAllUniqueStatisticsWithoutUris(start, end).stream()
                        .map(LogMapper::makeResponseDto)
                        .collect(Collectors.toList());
            }
            return rep.findAllUniqueStatistics(start, end, uris).stream()
                    .map(LogMapper::makeResponseDto)
                    .collect(Collectors.toList());
        } else {
            if (uris.isEmpty()) {
                return rep.findAllStatisticWithoutUris(start, end).stream()
                        .map(LogMapper::makeResponseDto)
                        .collect(Collectors.toList());
            }
            return rep.findAllStatistics(start, end, uris).stream()
                    .map(LogMapper::makeResponseDto)
                    .collect(Collectors.toList());
        }
    }
}
