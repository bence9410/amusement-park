package hu.bence.amusementpark.repository.custom.impl;

import hu.bence.amusementpark.dto.request.UserSearchRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.Photo_;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.entity.Users_;
import hu.bence.amusementpark.repository.custom.UserRepositoryCustom;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<UserResponseDto> findAll(UserSearchRequestDto dto, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        Long count = executeCountQuery(cb, dto);

        List<UserResponseDto> result = executeSearchQuery(cb, dto, pageable);

        return new PageImpl<>(result, pageable, count);
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<Users> root,
                                             UserSearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        ofNullable(dto.getName()).map(name -> cb.like(root.get(Users_.name), "%" + name + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getAuthority()).map(authority -> cb.like(cb.upper(root.get(Users_.authority)), "%" + authority.toUpperCase() + "%"))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinMoney()).map(minMoney -> cb.ge(root.get(Users_.money), minMoney))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxMoney()).map(maxMoney -> cb.le(root.get(Users_.money), maxMoney))
                .ifPresent(predicates::add);

        ofNullable(dto.getMinCoupon()).map(minCoupon -> cb.ge(root.get(Users_.coupon), minCoupon))
                .ifPresent(predicates::add);
        ofNullable(dto.getMaxCoupon()).map(maxCoupon -> cb.le(root.get(Users_.coupon), maxCoupon))
                .ifPresent(predicates::add);

        ofNullable(dto.getIsActivatedCoupon()).map(isActivatedCoupon -> cb.equal(root.get(Users_.isActivatedCoupon), isActivatedCoupon))
                .ifPresent(predicates::add);

        return predicates;
    }

    private Long executeCountQuery(CriteriaBuilder cb, UserSearchRequestDto dto) {
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Users> root = cq.from(Users.class);
        return entityManager
                .createQuery(cq.select(cb.count(root.get(Users_.name)))
                        .where(createPredicates(cb, root, dto).toArray(new Predicate[0])))
                .getSingleResult();
    }

    private List<UserResponseDto> executeSearchQuery(CriteriaBuilder cb, UserSearchRequestDto dto,
                                                     Pageable pageable) {
        CriteriaQuery<UserResponseDto> cq = cb.createQuery(UserResponseDto.class);
        Root<Users> root = cq.from(Users.class);

        Sort.Order order = pageable.getSortOr(Sort.by(Sort.Direction.DESC, "name")).stream().findFirst().get();
        if (order.getDirection().isAscending()) {
            cq.orderBy(cb.asc(root.get(order.getProperty())));
        } else {
            cq.orderBy(cb.desc(root.get(order.getProperty())));
        }

        cq.select(cb.construct(UserResponseDto.class, root.get(Users_.name), root.get(Users_.authority),
                        root.get(Users_.money), root.get(Users_.coupon), root.get(Users_.photo).get(Photo_.photo),
                        root.get(Users_.isActivatedCoupon)))
                .where(createPredicates(cb, root, dto).toArray(new Predicate[0]));
        return entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }
}
