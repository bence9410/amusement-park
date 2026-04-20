package hu.bence.amusementpark.service;

import hu.bence.amusementpark.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Users findByEmailMakeFreshlyLoggedIn(String userEmail);

    Users signUp(Users user);

    void uploadMoney(Integer amount, String userEmail);

    void leavePark(Long amusementParkId, String userEmail);

    Users enterPark(Long amusementParkId, String userEmail);

    Users getOnMachine(Long amusementParkId, Long machineId, String userEmail);

    void getOffMachine(Long amusementParkId, Long machineId, String userEmail);

}
