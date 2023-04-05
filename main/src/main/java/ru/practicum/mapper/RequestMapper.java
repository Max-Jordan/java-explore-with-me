package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RequestMapper {

    public static Request makeRequest(RequestDto dto) {
        Request request = new Request();
        request.setCreated(dto.getCreated());
        return request;
    }

    public static RequestDto makeRequestDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setRequester(request.getRequester().getId());
        dto.setId(request.getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
