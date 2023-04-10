package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;
import ru.practicum.statistics.mapper.LogMapper;
import ru.practicum.statistics.model.ViewStat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
//        if (unique) {
//            return uris.isEmpty() ? repository.findAllUniqueStatisticsWithoutUris(start, end).stream()
//                    .map(LogMapper::makeResponseDto)
//                    .collect(Collectors.toList()) :
//                    repository.findAllUniqueStatistics(start, end, endPoints).stream()
//                            .map(LogMapper::makeResponseDto)
//                            .collect(Collectors.toList());
//
//        } else {
//            return uris.isEmpty() ? repository.findAllStatisticWithoutUris(start, end).stream()
//                    .map(LogMapper::makeResponseDto)
//                    .collect(Collectors.toList()) :
//                    repository.findAllStatistics(start, end).stream()
//                            .map(LogMapper::makeResponseDto)
//                            .collect(Collectors.toList());
//        }
        LocalDateTime starts = LocalDateTime.of(2020,5,5,0,0,0);
        LocalDateTime ends = LocalDateTime.of(2023,5,5,0,0,0);
        System.out.println("Rep " + repository.test(starts, ends));
        List<ViewStat> viewStats = unique ? repository.findAllUniqueStatisticsWithoutUris(start, end)
                : repository.findAllStatisticWithoutUris(start, end);
        List<ViewStat> filterByUris = new ArrayList<>();
            for(String endPoint : uris) {
                filterByUris = viewStats.stream()
                        .filter(viewStat -> viewStat.getUri().contains(endPoint))
                        .collect(Collectors.toList());
            }
            return filterByUris.stream().sorted(Comparator.comparingLong(ViewStat::getHits)).map(LogMapper::makeResponseDto).collect(Collectors.toList());
        }
    }

