package hu.bence.amusementpark.service.impl;

import hu.bence.amusementpark.entity.*;
import hu.beni.amusementpark.entity.*;
import hu.bence.amusementpark.repository.AmusementParkKnowVisitorRepository;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.MachineRepository;
import hu.bence.amusementpark.repository.VisitorRepository;
import hu.bence.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
public class VisitorServiceImpl implements VisitorService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final VisitorRepository visitorRepository;
    private final AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) {
        Visitor visitor = visitorRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(COULD_NOT_FIND_USER, email)));
        return new User(email, visitor.getPassword(), List.of(new SimpleGrantedAuthority(visitor.getAuthority())));
    }

    @Override
    public Visitor findByEmailMakeFreshlyLoggedIn(String visitorEmail) {
        Visitor visitor = ifNull(visitorRepository.findById(visitorEmail),
                String.format(COULD_NOT_FIND_USER, visitorEmail));
        Optional.ofNullable(visitor.getPhoto()).map(Photo::getPhoto);
        visitor.setAmusementPark(null);
        visitor.setMachine(null);
        return visitor;
    }

    @Override
    public Visitor signUp(Visitor visitor) {
        ifNotZero(visitorRepository.countByEmail(visitor.getEmail()),
                String.format(EMAIL_ALREADY_TAKEN, visitor.getEmail()));
        visitor.setAuthority("ROLE_VISITOR");
        visitor.setMoney(250);
        visitor.setCoupon(0);
        visitor.setPassword(passwordEncoder.encode(visitor.getPassword()));
        return visitorRepository.save(visitor);
    }

    @Override
    public void uploadMoney(Integer amount, String visitorEmail) {
        visitorRepository.incrementSpendingMoneyByEmail(amount, visitorEmail);
    }

    @Override
    public Visitor enterPark(Long amusementParkId, String visitorEmail) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Visitor visitor = ifNull(visitorRepository.findById(visitorEmail), VISITOR_NOT_SIGNED_UP);
        checkIfVisitorAbleToEnterPark(amusementPark.getEntranceFee(), visitor);
        addToKnownVisitorsIfFirstEnter(amusementPark, visitor);
        incrementOwnerMoneyAndDecreaseMoney(amusementPark, visitor, amusementPark.getEntranceFee());
        visitor.setAmusementPark(amusementPark);
        return visitor;
    }

    private void checkIfVisitorAbleToEnterPark(Integer entranceFee, Visitor visitor) {
        ifFirstLessThanSecond(visitor.getMoney() + visitor.getCoupon(), entranceFee, NOT_ENOUGH_MONEY);
        ifNotNull(visitor.getAmusementPark(), VISITOR_IS_IN_A_PARK);
    }

    private void addToKnownVisitorsIfFirstEnter(AmusementPark amusementPark, Visitor visitor) {
        if (amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementPark.getId(),
                visitor.getEmail()) == 0) {
            amusementParkKnowVisitorRepository.save(new AmusementParkKnowVisitor(amusementPark, visitor));
        }
    }

    private void incrementOwnerMoneyAndDecreaseMoney(AmusementPark amusementPark, Visitor visitor, Integer amount) {
        if (!amusementPark.getOwner().equals(visitor)) {
            if (visitor.getCoupon() >= amount) {
                amusementPark.getOwner().setCoupon(amusementPark.getOwner().getCoupon() + amount);
                visitor.setCoupon(visitor.getCoupon() - amount);
            } else if (visitor.getCoupon() > 0) {
                amusementPark.getOwner().setCoupon(amusementPark.getOwner().getCoupon() + visitor.getCoupon());
                Integer leftOver = amount - visitor.getCoupon();
                visitor.setCoupon(0);
                amusementPark.getOwner().setMoney(amusementPark.getOwner().getMoney() + leftOver);
                visitor.setMoney(visitor.getMoney() - leftOver);
            } else {
                amusementPark.getOwner().setMoney(amusementPark.getOwner().getMoney() + amount);
                visitor.setMoney(visitor.getMoney() - amount);
            }
        }
    }

    @Override
    public Visitor getOnMachine(Long amusementParkId, Long machineId, String visitorEmail) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Machine machine = ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
                NO_MACHINE_IN_PARK_WITH_ID);
        Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail),
                NO_VISITOR_IN_PARK_WITH_ID);
        checkIfVisitorAbleToGetOnMachine(machine, visitor);
        incrementOwnerMoneyAndDecreaseMoney(amusementPark, visitor, machine.getTicketPrice());
        visitor.setMachine(machine);
        return visitor;
    }

    private void checkIfVisitorAbleToGetOnMachine(Machine machine, Visitor visitor) {
        ifNotNull(visitor.getMachine(), VISITOR_IS_ON_A_MACHINE);
        ifFirstLessThanSecond(visitor.getMoney() + visitor.getCoupon(), machine.getTicketPrice(), NOT_ENOUGH_MONEY);
        ifFirstLessThanSecond(Period.between(visitor.getDateOfBirth(), LocalDate.now()).getYears(),
                machine.getMinimumRequiredAge(), VISITOR_IS_TOO_YOUNG);
    }

    @Override
    public void getOffMachine(Long amusementParkId, Long machineId, String visitorEmail) {
        Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndMachineIdAndVisitorEmail(
                        amusementParkId, machineId, visitorEmail),
                NO_VISITOR_ON_MACHINE_WITH_ID);
        visitor.setMachine(null);
    }

    @Override
    public void leavePark(Long amusementParkId, String visitorEmail) {
        Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail),
                NO_VISITOR_IN_PARK_WITH_ID);
        visitor.setAmusementPark(null);
    }
}