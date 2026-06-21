package hu.bence.amusementpark.service.impl;

import hu.bence.amusementpark.dto.request.ModifyMoneyRequestDto;
import hu.bence.amusementpark.dto.request.UserSearchRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.*;
import hu.bence.amusementpark.repository.AmusementParkKnowUserRepository;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.MachineRepository;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.*;
import static hu.bence.amusementpark.exception.ExceptionUtil.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private final AmusementParkKnowUserRepository amusementParkKnowUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(@NonNull String name) {
        Users user = userRepository.findById(name)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(COULD_NOT_FIND_USER, name)));
        return new org.springframework.security.core.userdetails.User(name, user.getPassword(), List.of(new SimpleGrantedAuthority(user.getAuthority())));
    }

    @Override
    public Users findByNameMakeFreshlyLoggedIn(String userName) {
        Users user = ifNull(userRepository.findById(userName),
                String.format(COULD_NOT_FIND_USER, userName));
        Optional.ofNullable(user.getPhoto()).map(Photo::getPhoto);
        user.setAmusementPark(null);
        user.setMachine(null);
        return user;
    }

    @Override
    public Users signUp(Users user, String couponCode) {
        ifNotZero(userRepository.countByName(user.getName()),
                String.format(NAME_ALREADY_TAKEN, user.getName()));
        user.setAuthority("ROLE_VISITOR");
        user.setMoney(0);
        user.setCoupon(0);
        if (Objects.nonNull(couponCode) && !couponCode.isEmpty()) {
            ifNotEquals(couponCode, "EMPLOY_ME", WRONG_COUPON_CODE);
            user.setCoupon(15);
            user.setActivatedCoupon(true);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Users activateCoupon(String userName, String couponCode) {
        Users user = ifNull(userRepository.findById(userName),
                String.format(COULD_NOT_FIND_USER, userName));
        ifNotEquals(couponCode, "EMPLOY_ME", WRONG_COUPON_CODE);
        ifTrue(user.isActivatedCoupon(), ALREADY_ACTIVATED_COUPON_CODE);
        user.setCoupon(15);
        user.setActivatedCoupon(true);
        return user;
    }

    @Override
    public Users enterPark(Long amusementParkId, String userName) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Users user = ifNull(userRepository.findById(userName), USER_NOT_SIGNED_UP);
        checkIfUserAbleToEnterPark(amusementPark.getEntranceFee(), user);
        addToKnownUsersIfFirstEnter(amusementPark, user);
        incrementOwnerMoneyAndDecreaseMoney(amusementPark, user, amusementPark.getEntranceFee());
        user.setAmusementPark(amusementPark);
        return user;
    }

    private void checkIfUserAbleToEnterPark(Integer entranceFee, Users user) {
        ifFirstLessThanSecond(user.getMoney() + user.getCoupon(), entranceFee, NOT_ENOUGH_MONEY);
        ifNotNull(user.getAmusementPark(), USER_IS_IN_A_PARK);
    }

    private void addToKnownUsersIfFirstEnter(AmusementPark amusementPark, Users user) {
        if (amusementParkKnowUserRepository.countByAmusementParkIdAndUserName(amusementPark.getId(),
                user.getName()) == 0) {
            amusementParkKnowUserRepository.save(new AmusementParkKnowUser(amusementPark, user));
        }
    }

    private void incrementOwnerMoneyAndDecreaseMoney(AmusementPark amusementPark, Users user, Integer amount) {
        if (!amusementPark.getOwner().equals(user)) {
            if (user.getCoupon() >= amount) {
                userRepository.incrementCouponByEmail(amount, amusementPark.getOwner().getName());
                user.setCoupon(user.getCoupon() - amount);
            } else if (user.getCoupon() > 0) {
                userRepository.incrementCouponByEmail(user.getCoupon(), amusementPark.getOwner().getName());
                Integer leftOver = amount - user.getCoupon();
                user.setCoupon(0);
                userRepository.incrementMoneyByEmail(leftOver, amusementPark.getOwner().getName());
                user.setMoney(user.getMoney() - leftOver);
            } else {
                userRepository.incrementMoneyByEmail(amount, amusementPark.getOwner().getName());
                user.setMoney(user.getMoney() - amount);
            }
        }
    }

    @Override
    public Users getOnMachine(Long amusementParkId, Long machineId, String userName) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Machine machine = ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
                NO_MACHINE_IN_PARK_WITH_ID);
        Users user = ifNull(userRepository.findByAmusementParkIdAndUserName(amusementParkId, userName),
                NO_USER_IN_PARK_WITH_ID);
        checkIfUserAbleToGetOnMachine(machine, user);
        incrementOwnerMoneyAndDecreaseMoney(amusementPark, user, machine.getTicketPrice());
        user.setMachine(machine);
        return user;
    }

    private void checkIfUserAbleToGetOnMachine(Machine machine, Users user) {
        ifNotNull(user.getMachine(), USER_IS_ON_A_MACHINE);
        ifFirstLessThanSecond(user.getMoney() + user.getCoupon(), machine.getTicketPrice(), NOT_ENOUGH_MONEY);
        ifFirstLessThanSecond(Period.between(user.getDateOfBirth(), LocalDate.now()).getYears(),
                machine.getMinimumRequiredAge(), USER_IS_TOO_YOUNG);
    }

    @Override
    public void getOffMachine(Long amusementParkId, Long machineId, String userName) {
        Users user = ifNull(userRepository.findByAmusementParkIdAndMachineIdAndUserName(
                        amusementParkId, machineId, userName),
                NO_USER_ON_MACHINE_WITH_ID);
        user.setMachine(null);
    }

    @Override
    public void leavePark(Long amusementParkId, String userName) {
        Users user = ifNull(userRepository.findByAmusementParkIdAndUserName(amusementParkId, userName),
                NO_USER_IN_PARK_WITH_ID);
        user.setAmusementPark(null);
    }

    @Override
    public Page<UserResponseDto> findAll(UserSearchRequestDto dto, Pageable pageable) {
        return userRepository.findAll(dto, pageable);
    }

    @Override
    public void modifyMoney(ModifyMoneyRequestDto dto) {
        Users user = ifNull(userRepository.findById(dto.getUserName()),
                String.format(COULD_NOT_FIND_USER, dto.getUserName()));
        user.setMoney(user.getMoney() + dto.getValue());
    }

    @Override
    public void makeCreator(String userName) {
        Users user = ifNull(userRepository.findById(userName),
                String.format(COULD_NOT_FIND_USER, userName));
        ifNotEquals(user.getAuthority(), "ROLE_VISITOR", String.format(NOT_VISITOR, userName));
        user.setAuthority("ROLE_CREATOR");
    }
}