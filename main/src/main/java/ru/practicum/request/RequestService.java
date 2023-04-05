package ru.practicum.request;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.StatusRequest;

import java.util.List;

public interface RequestService {
    RequestDto saveRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUserId(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> findAllMemberRequests(Long eventId, Long userId);

    List<RequestDto> findAllByRequestsId(List<Long> requestsId);

    void updateRequest(Long idRequest, StatusRequest status);
}
