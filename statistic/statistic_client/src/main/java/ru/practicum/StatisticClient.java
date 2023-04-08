package ru.practicum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statistic.RequestStatDto;
import ru.practicum.statistic.ResponseStatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StatisticClient {

//    private static final String URL = "http://localhost:9090/";
//    private final RestTemplate restTemplate;
//
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    public void save(RequestStatDto stat) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<RequestStatDto> requestEntity = new HttpEntity<>(stat, headers);
//        restTemplate.exchange(URL + "hit", HttpMethod.POST, requestEntity, RequestStatDto.class);
//    }
//
//    public ResponseEntity<List<ResponseStatDto>> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris,
//                                                               Boolean unique) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
//
//        StringBuilder urisFull = new StringBuilder();
//        for (int i = 0; i < uris.size(); i++) {
//            if (i < (uris.size() - 1)) {
//                urisFull.append("uris").append("=").append(uris.get(i)).append(",");
//            } else {
//                urisFull.append("uris").append("=").append(uris.get(i));
//            }
//        }
//        Map<String, Object> parameters = Map.of(
//                "start", start.format(formatter),
//                "end", end.format(formatter),
//                "uris", urisFull.toString(),
//                "unique", unique);
//
//        String uri = URL + "stats" + "?start={start}&end={end}&uris={uris}&unique={unique}";
//
//        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {},
//                parameters);
//    }
//}

    private final String serverUrl;
    private final RestTemplate restTemplate;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatisticClient(@Value("${stats-server.url}") String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void save(RequestStatDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestStatDto> requestEntity = new HttpEntity<>(dto, headers);
        restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, RequestStatDto.class);
    }

    public List<ResponseStatDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));
        parameters.put("uris", uris);
        parameters.put("unique", unique);

        ResponseEntity<String> response = restTemplate.getForEntity(
                serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                String.class, parameters);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return Arrays.asList(objectMapper.readValue(response.getBody(), ResponseStatDto[].class));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("e");
        }
    }
}
