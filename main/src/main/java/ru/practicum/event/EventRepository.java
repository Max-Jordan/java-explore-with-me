package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.enums.StatusEvent;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e FROM Event e " +
            "WHERE  upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "or upper(e.title) like upper(concat('%', ?1, '%')) " +
            "AND e.paid = ?2 AND e.eventDate BETWEEN ?3 AND ?4 " +
            "AND e.category.id = ?5 AND e.state = 'PUBLISHED' ")
    Page<Event> findEventByAvailable(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> categories, Pageable page);


    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id = ?1 AND e.state = ?2 AND e.category.id = ?3 " +
            "AND e.eventDate BETWEEN ?4 AND ?5")
    Page<Event> findEventByInitiatorIdAndStateAndCategory_IdAndEventDateBetween(List<Long> users, List<StatusEvent> states,
                                                                                List<Long> categories, LocalDateTime rangeStart,
                                                                                LocalDateTime rangeEnd, Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE  upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            "or upper(e.title) like upper(concat('%', ?1, '%')) " +
            "AND e.paid = ?2 AND e.eventDate BETWEEN ?3 AND ?4 " +
            "AND e.category.id = ?5 AND e.state = 'PUBLISHED' ")
    Page<Event> findAllEvent(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> categories, Pageable page);
}
