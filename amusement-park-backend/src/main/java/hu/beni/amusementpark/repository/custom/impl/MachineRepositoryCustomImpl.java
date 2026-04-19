package hu.beni.amusementpark.repository.custom.impl;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark_;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Machine_;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.custom.MachineRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class MachineRepositoryCustomImpl implements MachineRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Long count = executeCountQuery(cb, dto);

        List<MachineSearchResponseDto> result = executeSearchQuery(cb, dto, pageable);

        return new PageImpl<>(result, pageable, count);
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<Machine> root, MachineSearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(Machine_.amusementPark).get(AmusementPark_.id), dto.getAmusementParkId()));

        ofNullable(dto.getFantasyName())
                .map(fantasyName -> cb.like(root.get(Machine_.fantasyName), "%" + fantasyName + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinMinimumRequiredAge())
                .map(minMinimumRequiredAge -> cb.ge(root.get(Machine_.minimumRequiredAge), minMinimumRequiredAge))
                .ifPresent(predicates::add);

        ofNullable(dto.getMaxMinimumRequiredAge())
                .map(maxMinimumRequiredAge -> cb.le(root.get(Machine_.minimumRequiredAge), maxMinimumRequiredAge))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinTicketPrice()).map(minTicketPrice -> cb.ge(root.get(Machine_.ticketPrice), minTicketPrice))
                .ifPresent(predicates::add);

        ofNullable(dto.getMaxTicketPrice()).map(maxTicketPrice -> cb.le(root.get(Machine_.ticketPrice), maxTicketPrice))
                .ifPresent(predicates::add);

        return predicates;
    }

    private Long executeCountQuery(CriteriaBuilder cb, MachineSearchRequestDto dto) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Machine> root = cq.from(Machine.class);

        Subquery<Long> countVisitors = cq.subquery(Long.class);
        Root<Machine> correlatedRoot = countVisitors.correlate(root);
        SetJoin<Root<Machine>, Visitor> machineSetJoin = correlatedRoot.joinSet(Machine_.visitors.getName());
        countVisitors.select(cb.count(machineSetJoin));

        List<Predicate> predicates = createPredicates(cb, root, dto);

        ofNullable(dto.getMinNumberOfVisitorsOnMachine()).map(minNumberOfVisitorsOnMachine -> cb.ge(countVisitors, minNumberOfVisitorsOnMachine))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxNumberOfVisitorsOnMachine()).map(maxNumberOfVisitorsOnMachine -> cb.le(countVisitors, maxNumberOfVisitorsOnMachine))
                .ifPresent(predicates::add);

        return entityManager
                .createQuery(cq.select(cb.count(root.get(Machine_.id)))
                        .where(predicates.toArray(new Predicate[0])))
                .getSingleResult();
    }

    private List<MachineSearchResponseDto> executeSearchQuery(CriteriaBuilder cb, MachineSearchRequestDto dto,
                                                              Pageable pageable) {
        CriteriaQuery<MachineSearchResponseDto> cq = cb.createQuery(MachineSearchResponseDto.class);
        Root<Machine> root = cq.from(Machine.class);

        Subquery<Long> countVisitors = cq.subquery(Long.class);
        Root<Machine> correlatedRoot = countVisitors.correlate(root);
        SetJoin<Root<Machine>, Visitor> machineSetJoin = correlatedRoot.joinSet(Machine_.visitors.getName());
        countVisitors.select(cb.count(machineSetJoin));

        Order order = pageable.getSortOr(Sort.by(Direction.DESC, "id")).stream().findFirst().get();
        if (order.getProperty().equals("numberOfVisitorsOnMachine")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countVisitors));
            } else {
                cq.orderBy(cb.desc(countVisitors));
            }
        } else {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(root.get(order.getProperty())));
            } else {
                cq.orderBy(cb.desc(root.get(order.getProperty())));
            }
        }

        List<Predicate> predicates = createPredicates(cb, root, dto);

        ofNullable(dto.getMinNumberOfVisitorsOnMachine()).map(minNumberOfVisitorsOnMachine -> cb.ge(countVisitors, minNumberOfVisitorsOnMachine))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxNumberOfVisitorsOnMachine()).map(maxNumberOfVisitorsOnMachine -> cb.le(countVisitors, maxNumberOfVisitorsOnMachine))
                .ifPresent(predicates::add);

        cq.select(cb.construct(MachineSearchResponseDto.class, root.get(Machine_.id), root.get(Machine_.fantasyName),
                        root.get(Machine_.minimumRequiredAge), root.get(Machine_.ticketPrice), root.get(Machine_.video),
                        root.get(Machine_.videoLengthInSeconds), countVisitors))
                .where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }

}
