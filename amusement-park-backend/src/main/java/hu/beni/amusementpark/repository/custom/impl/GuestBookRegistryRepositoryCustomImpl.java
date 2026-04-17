package hu.beni.amusementpark.repository.custom.impl;

import hu.beni.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.beni.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark_;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.GuestBookRegistry_;
import hu.beni.amusementpark.entity.Visitor_;
import hu.beni.amusementpark.repository.custom.GuestBookRegistryRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class GuestBookRegistryRepositoryCustomImpl implements GuestBookRegistryRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<GuestBookRegistrySearchResponseDto> findAll(GuestBookRegistrySearchRequestDto dto, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Long count = executeCountQuery(cb, dto);

        List<GuestBookRegistrySearchResponseDto> result = executeSearchQuery(cb, dto, pageable);

        return new PageImpl<>(result, pageable, count);
    }

    private Predicate[] createPredicates(CriteriaBuilder cb, Root<GuestBookRegistry> root,
                                         GuestBookRegistrySearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(
                cb.equal(root.get(GuestBookRegistry_.amusementPark).get(AmusementPark_.id), dto.getAmusementParkId()));

        ofNullable(dto.getVisitorEmail()).map(visitorEmail -> cb
                        .like(root.get(GuestBookRegistry_.visitor).get(Visitor_.email), "%" + visitorEmail + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getTextOfRegistry()).map(textOfRegistry -> cb
                        .like(root.get(GuestBookRegistry_.textOfRegistry), "%" + textOfRegistry + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinDateOfRegistry()).map(minDateOfRegistry -> cb
                        .greaterThanOrEqualTo(root.get(GuestBookRegistry_.dateOfRegistry), minDateOfRegistry))
                .ifPresent(predicates::add);

        ofNullable(dto.getMaxDateOfRegistry()).map(maxDateOfRegistry -> cb
                        .lessThanOrEqualTo(root.get(GuestBookRegistry_.dateOfRegistry), maxDateOfRegistry))
                .ifPresent(predicates::add);

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    private Long executeCountQuery(CriteriaBuilder cb, GuestBookRegistrySearchRequestDto dto) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<GuestBookRegistry> root = cq.from(GuestBookRegistry.class);
        return entityManager
                .createQuery(
                        cq.select(cb.count(root.get(GuestBookRegistry_.id))).where(createPredicates(cb, root, dto)))
                .getSingleResult();
    }

    private List<GuestBookRegistrySearchResponseDto> executeSearchQuery(CriteriaBuilder cb,
                                                                        GuestBookRegistrySearchRequestDto dto, Pageable pageable) {
        CriteriaQuery<GuestBookRegistrySearchResponseDto> cq = cb.createQuery(GuestBookRegistrySearchResponseDto.class);
        Root<GuestBookRegistry> root = cq.from(GuestBookRegistry.class);

        Order order = pageable.getSortOr(Sort.by(Direction.DESC, "id")).stream().findFirst().get();
        if (order.getDirection().isAscending()) {
            cq.orderBy(cb.asc(root.get(order.getProperty())));
        } else {
            cq.orderBy(cb.desc(root.get(order.getProperty())));
        }

        cq.multiselect(root.get(GuestBookRegistry_.visitor).get(Visitor_.email),
                        root.get(GuestBookRegistry_.textOfRegistry), root.get(GuestBookRegistry_.dateOfRegistry))
                .where(createPredicates(cb, root, dto));
        return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }

}
