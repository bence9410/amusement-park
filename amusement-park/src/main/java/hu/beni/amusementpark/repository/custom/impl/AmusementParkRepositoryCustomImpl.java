package hu.beni.amusementpark.repository.custom.impl;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.entity.*;
import hu.beni.amusementpark.repository.custom.AmusementParkRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class AmusementParkRepositoryCustomImpl implements AmusementParkRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<AmusementParkDetailResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Long count = executeCountQuery(cb, dto);

        List<AmusementParkDetailResponseDto> result = executeSearchQuery(cb, dto, pageable);

        return new PageImpl<>(result, pageable, count);
    }

    private Predicate[] createPredicates(CriteriaBuilder cb, Root<AmusementPark> root,
                                         AmusementParkSearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        ofNullable(dto.getName()).map(name -> cb.like(root.get(AmusementPark_.name), "%" + name + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getCapitalMin()).map(capitalMin -> cb.ge(root.get(AmusementPark_.capital), capitalMin))
                .ifPresent(predicates::add);
        ofNullable(dto.getCapitalMax()).map(capitalMax -> cb.le(root.get(AmusementPark_.capital), capitalMax))
                .ifPresent(predicates::add);

        ofNullable(dto.getTotalAreaMin()).map(totalAreaMin -> cb.ge(root.get(AmusementPark_.totalArea), totalAreaMin))
                .ifPresent(predicates::add);
        ofNullable(dto.getTotalAreaMax()).map(totalAreaMax -> cb.le(root.get(AmusementPark_.totalArea), totalAreaMax))
                .ifPresent(predicates::add);

        ofNullable(dto.getEntranceFeeMin())
                .map(entranceFeeMin -> cb.ge(root.get(AmusementPark_.entranceFee), entranceFeeMin))
                .ifPresent(predicates::add);
        ofNullable(dto.getEntranceFeeMax())
                .map(entranceFeeMax -> cb.le(root.get(AmusementPark_.entranceFee), entranceFeeMax))
                .ifPresent(predicates::add);

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    private Long executeCountQuery(CriteriaBuilder cb, AmusementParkSearchRequestDto dto) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AmusementPark> root = cq.from(AmusementPark.class);
        return entityManager
                .createQuery(cq.select(cb.count(root.get(AmusementPark_.id))).where(createPredicates(cb, root, dto)))
                .getSingleResult();
    }

    private List<AmusementParkDetailResponseDto> executeSearchQuery(CriteriaBuilder cb,
                                                                    AmusementParkSearchRequestDto dto, Pageable pageable) {
        CriteriaQuery<AmusementParkDetailResponseDto> cq = cb.createQuery(AmusementParkDetailResponseDto.class);
        Root<AmusementPark> root = cq.from(AmusementPark.class);

        Order order = pageable.getSortOr(Sort.by(Direction.DESC, "id")).stream().findFirst().get();
        if (order.getDirection().isAscending()) {
            cq.orderBy(cb.asc(root.get(order.getProperty())));
        } else {
            cq.orderBy(cb.desc(root.get(order.getProperty())));
        }

        Subquery<Long> countMachines = cq.subquery(Long.class);
        Root<Machine> machineRoot = countMachines.from(Machine.class);
        countMachines.where(cb.equal(root, machineRoot.get(Machine_.amusementPark)));
        countMachines.select(cb.count(machineRoot));

        Subquery<Long> countGuestBooks = cq.subquery(Long.class);
        Root<GuestBookRegistry> guestBookRoot = countGuestBooks.from(GuestBookRegistry.class);
        countGuestBooks.where(cb.equal(root, guestBookRoot.get(GuestBookRegistry_.amusementPark)));
        countGuestBooks.select(cb.count(guestBookRoot));

        Subquery<Long> countActiveVisitors = cq.subquery(Long.class);
        Root<Visitor> activeRoot = countActiveVisitors.from(Visitor.class);
        countActiveVisitors.where(cb.equal(root, activeRoot.get(Visitor_.amusementPark)));
        countActiveVisitors.select(cb.count(activeRoot));

        Subquery<Long> countKnownVisitors = cq.subquery(Long.class);
        Root<AmusementParkKnowVisitor> knownRoot = countKnownVisitors.from(AmusementParkKnowVisitor.class);
        countKnownVisitors.where(cb.equal(root, knownRoot.get(AmusementParkKnowVisitor_.amusementPark)));
        countKnownVisitors.select(cb.count(knownRoot));

        cq.multiselect(root.get(AmusementPark_.id), root.get(AmusementPark_.name), root.get(AmusementPark_.capital),
                        root.get(AmusementPark_.totalArea), root.get(AmusementPark_.entranceFee), countMachines.getSelection(),
                        countGuestBooks.getSelection(), countActiveVisitors.getSelection(), countKnownVisitors.getSelection())
                .where(createPredicates(cb, root, dto));
        return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }

}
