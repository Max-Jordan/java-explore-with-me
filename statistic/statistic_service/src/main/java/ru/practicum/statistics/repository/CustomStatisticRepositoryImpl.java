package ru.practicum.statistics.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.statistics.model.EndpointHit;
import ru.practicum.statistics.model.ViewStat;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomStatisticRepositoryImpl implements CustomStatisticRepository {

    private final EntityManager em;

    @Override
    public List<ViewStat> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ViewStat> cq = criteriaBuilder.createQuery(ViewStat.class);

        Root<EndpointHit> statsEntityRoot = cq.from(EndpointHit.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.between(statsEntityRoot.get("timestamp"), start, end));

        if (uris != null && !uris.isEmpty()) {
            predicates.add(criteriaBuilder.isTrue(statsEntityRoot.get("uri").in(uris)));
        }

        cq.multiselect(statsEntityRoot.get("app"), statsEntityRoot.get("uri"),
                unique != null && unique
                        ? criteriaBuilder.countDistinct(statsEntityRoot.get("ip"))
                        : criteriaBuilder.count(statsEntityRoot.get("ip")));

        cq.groupBy(statsEntityRoot.get("uri"), statsEntityRoot.get("app"));
        cq.orderBy(criteriaBuilder.desc(criteriaBuilder.literal(3)));
        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}
