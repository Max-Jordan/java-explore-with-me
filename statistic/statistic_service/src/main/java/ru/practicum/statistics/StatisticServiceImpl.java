package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.statistics.mapper.LogMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.statistics.mapper.LogMapper.makeLog;
import static ru.practicum.statistics.mapper.LogMapper.makeRequestDto;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository repository;

    @Override
    @Transactional
    public RequestStatDto save(RequestStatDto dto) {
        return makeRequestDto(repository.save(makeLog(dto)));
    }

    @Override
    public List<ResponseStatDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return uris.isEmpty() ? repository.findAllUniqueStatisticsWithoutUris(start, end).stream()
                    .map(LogMapper::makeResponseDto)
                    .collect(Collectors.toList()) :
                    repository.findAllUniqueStatistics(start, end, uris).stream()
                            .map(LogMapper::makeResponseDto)
                            .collect(Collectors.toList());

        } else {
            return uris.isEmpty() ? repository.findAllStatisticWithoutUris(start, end).stream()
                    .map(LogMapper::makeResponseDto)
                    .collect(Collectors.toList()) :
                    repository.findAllStatistics(start, end, uris).stream()
                            .map(LogMapper::makeResponseDto)
                            .collect(Collectors.toList());
        }
    }
}

