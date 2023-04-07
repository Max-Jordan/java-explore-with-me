package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RequestMapper {

    public static Request makeRequest(ParticipationRequestDto dto) {
        Request request = new Request();
        request.setCreated(dto.getCreated());
        return request;
    }

    public static ParticipationRequestDto makeParticipationRequestDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setRequester(request.getRequester().getId());
        dto.setId(request.getId());
        dto.setEvent(request.getEvent().getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
