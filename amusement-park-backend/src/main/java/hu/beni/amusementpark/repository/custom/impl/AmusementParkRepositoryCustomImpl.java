package hu.beni.amusementpark.repository.custom.impl;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.beni.amusementpark.entity.*;
import hu.beni.amusementpark.repository.custom.AmusementParkRepositoryCustom;
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
public class AmusementParkRepositoryCustomImpl implements AmusementParkRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<AmusementParkSearchResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Long count = executeCountQuery(cb, dto);

        List<AmusementParkSearchResponseDto> result = executeSearchQuery(cb, dto, pageable);

        return new PageImpl<>(result, pageable, count);
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<AmusementPark> root,
                                             AmusementParkSearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        ofNullable(dto.getName()).map(name -> cb.like(root.get(AmusementPark_.name), "%" + name + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinCapital()).map(minCapital -> cb.ge(root.get(AmusementPark_.capital), minCapital))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxCapital()).map(maxCapital -> cb.le(root.get(AmusementPark_.capital), maxCapital))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinTotalArea()).map(minTotalArea -> cb.ge(root.get(AmusementPark_.totalArea), minTotalArea))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxTotalArea()).map(maxTotalArea -> cb.le(root.get(AmusementPark_.totalArea), maxTotalArea))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinEntranceFee()).map(minEntranceFee -> cb.ge(root.get(AmusementPark_.entranceFee), minEntranceFee))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxEntranceFee()).map(maxEntranceFee -> cb.le(root.get(AmusementPark_.entranceFee), maxEntranceFee))
                .ifPresent(predicates::add);

        return predicates;
    }

    private Long executeCountQuery(CriteriaBuilder cb, AmusementParkSearchRequestDto dto) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmusementPark> root = cq.from(AmusementPark.class);

        Subquery<Long> countMachines = cq.subquery(Long.class);
        Root<AmusementPark> correlatedRoot = countMachines.correlate(root);
        SetJoin<Root<AmusementPark>, Machine> machineSetJoin = correlatedRoot.joinSet(AmusementPark_.machines.getName());
        countMachines.select(cb.count(machineSetJoin));

        Subquery<Long> countGuestBookRegistries = cq.subquery(Long.class);
        correlatedRoot = countGuestBookRegistries.correlate(root);
        SetJoin<Root<AmusementPark>, GuestBookRegistry> guestBookRegistrySetJoin = correlatedRoot.joinSet(AmusementPark_.guestBookRegistries.getName());
        countGuestBookRegistries.select(cb.count(guestBookRegistrySetJoin));

        Subquery<Long> countActiveVisitors = cq.subquery(Long.class);
        correlatedRoot = countActiveVisitors.correlate(root);
        SetJoin<Root<AmusementPark>, Visitor> activeVisitorSetJoin = correlatedRoot.joinSet(AmusementPark_.activeVisitors.getName());
        countActiveVisitors.select(cb.count(activeVisitorSetJoin));

        Subquery<Long> countKnownVisitors = cq.subquery(Long.class);
        correlatedRoot = countKnownVisitors.correlate(root);
        SetJoin<Root<AmusementPark>, Visitor> knownVisitorSetJoin = correlatedRoot.joinSet(AmusementPark_.knownVisitors.getName());
        countKnownVisitors.select(cb.count(knownVisitorSetJoin));

        List<Predicate> predicates = createPredicates(cb, root, dto);

        ofNullable(dto.getMinMachines()).map(minMachines -> cb.ge(countMachines, minMachines))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxMachines()).map(maxMachines -> cb.le(countMachines, maxMachines))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinGuestBookRegistries()).map(minGuestBookRegistries -> cb.ge(countGuestBookRegistries, minGuestBookRegistries))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxGuestBookRegistries()).map(maxGuestBookRegistries -> cb.le(countGuestBookRegistries, maxGuestBookRegistries))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinActiveVisitors()).map(minActiveVisitors -> cb.ge(countActiveVisitors, minActiveVisitors))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxActiveVisitors()).map(maxActiveVisitors -> cb.le(countActiveVisitors, maxActiveVisitors))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinKnownVisitors()).map(minKnownVisitors -> cb.ge(countKnownVisitors, minKnownVisitors))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxKnownVisitors()).map(maxKnownVisitors -> cb.le(countKnownVisitors, maxKnownVisitors))
                .ifPresent(predicates::add);

        return entityManager
                .createQuery(cq.select(cb.count(root.get(AmusementPark_.id)))
                        .where(predicates.toArray(new Predicate[0])))
                .getSingleResult();
    }

    private List<AmusementParkSearchResponseDto> executeSearchQuery(CriteriaBuilder cb,
                                                                    AmusementParkSearchRequestDto dto, Pageable pageable) {
        CriteriaQuery<AmusementParkSearchResponseDto> cq = cb.createQuery(AmusementParkSearchResponseDto.class);
        Root<AmusementPark> root = cq.from(AmusementPark.class);

        Subquery<Long> countMachines = cq.subquery(Long.class);
        Root<AmusementPark> correlatedRoot = countMachines.correlate(root);
        SetJoin<Root<AmusementPark>, Machine> machineSetJoin = correlatedRoot.joinSet(AmusementPark_.machines.getName());
        countMachines.select(cb.count(machineSetJoin));

        Subquery<Long> countGuestBookRegistries = cq.subquery(Long.class);
        correlatedRoot = countGuestBookRegistries.correlate(root);
        SetJoin<Root<AmusementPark>, GuestBookRegistry> guestBookRegistrySetJoin = correlatedRoot.joinSet(AmusementPark_.guestBookRegistries.getName());
        countGuestBookRegistries.select(cb.count(guestBookRegistrySetJoin));

        Subquery<Long> countActiveVisitors = cq.subquery(Long.class);
        correlatedRoot = countActiveVisitors.correlate(root);
        SetJoin<Root<AmusementPark>, Visitor> activeVisitorSetJoin = correlatedRoot.joinSet(AmusementPark_.activeVisitors.getName());
        countActiveVisitors.select(cb.count(activeVisitorSetJoin));

        Subquery<Long> countKnownVisitors = cq.subquery(Long.class);
        correlatedRoot = countKnownVisitors.correlate(root);
        SetJoin<Root<AmusementPark>, Visitor> knownVisitorSetJoin = correlatedRoot.joinSet(AmusementPark_.knownVisitors.getName());
        countKnownVisitors.select(cb.count(knownVisitorSetJoin));

        Order order = pageable.getSortOr(Sort.by(Direction.ASC, "id")).stream().findFirst().get();
        if (order.getProperty().equals("numberOfMachines")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countMachines));
            } else {
                cq.orderBy(cb.desc(countMachines));
            }
        } else if (order.getProperty().equals("numberOfGuestBookRegistries")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countGuestBookRegistries));
            } else {
                cq.orderBy(cb.desc(countGuestBookRegistries));
            }
        } else if (order.getProperty().equals("numberOfActiveVisitors")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countActiveVisitors));
            } else {
                cq.orderBy(cb.desc(countActiveVisitors));
            }
        } else if (order.getProperty().equals("numberOfKnownVisitors")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countKnownVisitors));
            } else {
                cq.orderBy(cb.desc(countKnownVisitors));
            }
        } else {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(root.get(order.getProperty())));
            } else {
                cq.orderBy(cb.desc(root.get(order.getProperty())));
            }
        }

        List<Predicate> predicates = createPredicates(cb, root, dto);

        ofNullable(dto.getMinMachines()).map(minMachines -> cb.ge(countMachines, minMachines))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxMachines()).map(maxMachines -> cb.le(countMachines, maxMachines))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinGuestBookRegistries()).map(minGuestBookRegistries -> cb.ge(countGuestBookRegistries, minGuestBookRegistries))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxGuestBookRegistries()).map(maxGuestBookRegistries -> cb.le(countGuestBookRegistries, maxGuestBookRegistries))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinActiveVisitors()).map(minActiveVisitors -> cb.ge(countActiveVisitors, minActiveVisitors))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxActiveVisitors()).map(maxActiveVisitors -> cb.le(countActiveVisitors, maxActiveVisitors))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinKnownVisitors()).map(minKnownVisitors -> cb.ge(countKnownVisitors, minKnownVisitors))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxKnownVisitors()).map(maxKnownVisitors -> cb.le(countKnownVisitors, maxKnownVisitors))
                .ifPresent(predicates::add);

        cq.select(cb.construct(AmusementParkSearchResponseDto.class, root.get(AmusementPark_.id), root.get(AmusementPark_.name), root.get(AmusementPark_.capital),
                        root.get(AmusementPark_.totalArea), root.get(AmusementPark_.entranceFee), countMachines,
                        countGuestBookRegistries, countActiveVisitors, countKnownVisitors))
                .where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }

}
