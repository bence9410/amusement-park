package hu.bence.amusementpark.repository.custom.impl;

import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.entity.*;
import hu.bence.amusementpark.repository.custom.AmusementParkRepositoryCustom;
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

        Subquery<Long> countActiveUsers = cq.subquery(Long.class);
        correlatedRoot = countActiveUsers.correlate(root);
        SetJoin<Root<AmusementPark>, Users> activeUserSetJoin = correlatedRoot.joinSet(AmusementPark_.activeUsers.getName());
        countActiveUsers.select(cb.count(activeUserSetJoin));

        Subquery<Long> countKnownUsers = cq.subquery(Long.class);
        correlatedRoot = countKnownUsers.correlate(root);
        SetJoin<Root<AmusementPark>, Users> knownUserSetJoin = correlatedRoot.joinSet(AmusementPark_.knownUsers.getName());
        countKnownUsers.select(cb.count(knownUserSetJoin));

        List<Predicate> predicates = createPredicates(cb, root, dto);

        ofNullable(dto.getMinMachines()).map(minMachines -> cb.ge(countMachines, minMachines))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxMachines()).map(maxMachines -> cb.le(countMachines, maxMachines))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinGuestBookRegistries()).map(minGuestBookRegistries -> cb.ge(countGuestBookRegistries, minGuestBookRegistries))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxGuestBookRegistries()).map(maxGuestBookRegistries -> cb.le(countGuestBookRegistries, maxGuestBookRegistries))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinActiveUsers()).map(minActiveUsers -> cb.ge(countActiveUsers, minActiveUsers))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxActiveUsers()).map(maxActiveUsers -> cb.le(countActiveUsers, maxActiveUsers))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinKnownUsers()).map(minKnownUsers -> cb.ge(countKnownUsers, minKnownUsers))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxKnownUsers()).map(maxKnownUsers -> cb.le(countKnownUsers, maxKnownUsers))
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

        Subquery<Long> countActiveUsers = cq.subquery(Long.class);
        correlatedRoot = countActiveUsers.correlate(root);
        SetJoin<Root<AmusementPark>, Users> activeUserSetJoin = correlatedRoot.joinSet(AmusementPark_.activeUsers.getName());
        countActiveUsers.select(cb.count(activeUserSetJoin));

        Subquery<Long> countKnownUsers = cq.subquery(Long.class);
        correlatedRoot = countKnownUsers.correlate(root);
        SetJoin<Root<AmusementPark>, Users> knownUserSetJoin = correlatedRoot.joinSet(AmusementPark_.knownUsers.getName());
        countKnownUsers.select(cb.count(knownUserSetJoin));

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
        } else if (order.getProperty().equals("numberOfActiveUsers")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countActiveUsers));
            } else {
                cq.orderBy(cb.desc(countActiveUsers));
            }
        } else if (order.getProperty().equals("numberOfKnownUsers")) {
            if (order.getDirection().isAscending()) {
                cq.orderBy(cb.asc(countKnownUsers));
            } else {
                cq.orderBy(cb.desc(countKnownUsers));
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

        ofNullable(dto.getMinActiveUsers()).map(minActiveUsers -> cb.ge(countActiveUsers, minActiveUsers))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxActiveUsers()).map(maxActiveUsers -> cb.le(countActiveUsers, maxActiveUsers))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinKnownUsers()).map(minKnownUsers -> cb.ge(countKnownUsers, minKnownUsers))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxKnownUsers()).map(maxKnownUsers -> cb.le(countKnownUsers, maxKnownUsers))
                .ifPresent(predicates::add);

        cq.select(cb.construct(AmusementParkSearchResponseDto.class, root.get(AmusementPark_.id), root.get(AmusementPark_.name),
                        root.get(AmusementPark_.entranceFee), root.get(AmusementPark_.owner).get(Users_.email), countMachines,
                        countGuestBookRegistries, countActiveUsers, countKnownUsers))
                .where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }

}
