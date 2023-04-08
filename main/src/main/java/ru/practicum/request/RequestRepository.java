package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.dto.StatusRequest;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    Request findRequestByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findByEventId(Long evenId);

    List<Request> findAllRequestByIdIn(List<Long> requestsId);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Integer countAllByStatus(StatusRequest status);
}
