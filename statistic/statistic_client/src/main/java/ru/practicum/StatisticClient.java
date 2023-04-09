package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statistic.ResponseStatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticClient extends BaseClient {
    @Autowired
    public StatisticClient(@Value("${statistic-server.url}") String url, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void save(HttpServletRequest request) {
        String app = "main-app";
        NewStat newStat = new NewStat(app, request.getRequestURI(), request.getRemoteAddr());
        post("/hit", newStat);
    }

    public Long getViewForEvent(Long eventId) {
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> parameters = Map.of(
                "start", LocalDateTime.now().minusYears(10).format(format),
                "end", LocalDateTime.now().plusYears(10).format(format),
                "uris", (List.of("/events/" + eventId)),
                "unique", "false"
        );
        ResponseEntity<Object> response = get(url, parameters);

        List<ResponseStatDto> viewStatsList = response.hasBody() ? (List<ResponseStatDto>) response.getBody() : Collections.EMPTY_LIST;
        return viewStatsList != null && !viewStatsList.isEmpty() ? viewStatsList.get(0).getHits() : 0L;
    }

    public List<ResponseStatDto> getAllView(Set<Long> eventsId) {
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> parameters = Map.of(
                "start",  LocalDateTime.now().minusYears(10).format(format),
                "end", LocalDateTime.now().plusYears(10).format(format),
                "uris", (eventsId.stream().map(id -> "/events/" + id).collect(Collectors.toList())),
                "unique", "false"
        );
        ResponseEntity<Object> response = get(url, parameters);
        return response.hasBody() ? (List<ResponseStatDto>) response.getBody() : Collections.EMPTY_LIST;
    }
}