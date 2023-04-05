package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    Request findRequestByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findAllByEventIdAndRequesterId(Long evenId, Long userId);

    List<Request> findAllRequestByIdIn(List<Long> requestsId);
}
