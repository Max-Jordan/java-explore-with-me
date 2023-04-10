package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.statistics.mapper.LogMapper;
import ru.practicum.statistics.repository.StatisticRepository;

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
        if (start == null) {
            start = LocalDateTime.now();
        }
        return repository.getStatistic(start, end, uris, unique).stream()
                .map(LogMapper::makeResponseDto)
                .collect(Collectors.toList());
    }
}

