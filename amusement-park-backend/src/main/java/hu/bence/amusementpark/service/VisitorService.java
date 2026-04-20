package hu.bence.amusementpark.service;

import hu.bence.amusementpark.entity.Visitor;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface VisitorService extends UserDetailsService {

    Visitor findByEmailMakeFreshlyLoggedIn(String visitorEmail);

    Visitor signUp(Visitor visitor);

    void uploadMoney(Integer amount, String visitorEmail);

    void leavePark(Long amusementParkId, String visitorEmail);

    Visitor enterPark(Long amusementParkId, String visitorEmail);

    Visitor getOnMachine(Long amusementParkId, Long machineId, String visitorEmail);

    void getOffMachine(Long amusementParkId, Long machineId, String visitorEmail);

}
