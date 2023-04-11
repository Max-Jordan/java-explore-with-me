package ru.practicum.request;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.StatusRequest;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> findAllRequests(Long eventId);

    List<ParticipationRequestDto> findAllByRequestsId(List<Long> requestsId);

    void updateRequest(Long idRequest, StatusRequest status);

    Integer findRequestByStatus(StatusRequest status);
}
