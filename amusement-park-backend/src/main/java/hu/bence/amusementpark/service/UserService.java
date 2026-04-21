package hu.bence.amusementpark.service;

import hu.bence.amusementpark.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Users findByNameMakeFreshlyLoggedIn(String userName);

    Users signUp(Users user, String couponCode);

    void uploadMoney(Integer amount, String userName);

    Users activateCoupon(String userName, String couponCode);

    void leavePark(Long amusementParkId, String userName);

    Users enterPark(Long amusementParkId, String userName);

    Users getOnMachine(Long amusementParkId, Long machineId, String userName);

    void getOffMachine(Long amusementParkId, Long machineId, String userName);

}
