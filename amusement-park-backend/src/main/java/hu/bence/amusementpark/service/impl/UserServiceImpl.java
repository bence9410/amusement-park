package hu.bence.amusementpark.service.impl;

import hu.bence.amusementpark.entity.*;
import hu.bence.amusementpark.repository.AmusementParkKnowUserRepository;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.MachineRepository;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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
    public UserDetails loadUserByUsername(@NonNull String email) {
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(COULD_NOT_FIND_USER, email)));
        return new org.springframework.security.core.userdetails.User(email, user.getPassword(), List.of(new SimpleGrantedAuthority(user.getAuthority())));
    }

    @Override
    public Users findByEmailMakeFreshlyLoggedIn(String userEmail) {
        Users user = ifNull(userRepository.findById(userEmail),
                String.format(COULD_NOT_FIND_USER, userEmail));
        Optional.ofNullable(user.getPhoto()).map(Photo::getPhoto);
        user.setAmusementPark(null);
        user.setMachine(null);
        return user;
    }

    @Override
    public Users signUp(Users user) {
        ifNotZero(userRepository.countByEmail(user.getEmail()),
                String.format(EMAIL_ALREADY_TAKEN, user.getEmail()));
        user.setAuthority("ROLE_VISITOR");
        user.setMoney(250);
        user.setCoupon(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void uploadMoney(Integer amount, String userEmail) {
        userRepository.incrementSpendingMoneyByEmail(amount, userEmail);
    }

    @Override
    public Users enterPark(Long amusementParkId, String userEmail) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Users user = ifNull(userRepository.findById(userEmail), USER_NOT_SIGNED_UP);
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
        if (amusementParkKnowUserRepository.countByAmusementParkIdAndUserEmail(amusementPark.getId(),
                user.getEmail()) == 0) {
            amusementParkKnowUserRepository.save(new AmusementParkKnowUser(amusementPark, user));
        }
    }

    private void incrementOwnerMoneyAndDecreaseMoney(AmusementPark amusementPark, Users user, Integer amount) {
        if (!amusementPark.getOwner().equals(user)) {
            if (user.getCoupon() >= amount) {
                amusementPark.getOwner().setCoupon(amusementPark.getOwner().getCoupon() + amount);
                user.setCoupon(user.getCoupon() - amount);
            } else if (user.getCoupon() > 0) {
                amusementPark.getOwner().setCoupon(amusementPark.getOwner().getCoupon() + user.getCoupon());
                Integer leftOver = amount - user.getCoupon();
                user.setCoupon(0);
                amusementPark.getOwner().setMoney(amusementPark.getOwner().getMoney() + leftOver);
                user.setMoney(user.getMoney() - leftOver);
            } else {
                amusementPark.getOwner().setMoney(amusementPark.getOwner().getMoney() + amount);
                user.setMoney(user.getMoney() - amount);
            }
        }
    }

    @Override
    public Users getOnMachine(Long amusementParkId, Long machineId, String userEmail) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Machine machine = ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
                NO_MACHINE_IN_PARK_WITH_ID);
        Users user = ifNull(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, userEmail),
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
    public void getOffMachine(Long amusementParkId, Long machineId, String userEmail) {
        Users user = ifNull(userRepository.findByAmusementParkIdAndMachineIdAndUserEmail(
                        amusementParkId, machineId, userEmail),
                NO_USER_ON_MACHINE_WITH_ID);
        user.setMachine(null);
    }

    @Override
    public void leavePark(Long amusementParkId, String userEmail) {
        Users user = ifNull(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, userEmail),
                NO_USER_IN_PARK_WITH_ID);
        user.setAmusementPark(null);
    }
}