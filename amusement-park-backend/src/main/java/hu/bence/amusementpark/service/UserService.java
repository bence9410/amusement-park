package hu.bence.amusementpark.service;

import hu.bence.amusementpark.dto.request.ModifyMoneyRequestDto;
import hu.bence.amusementpark.dto.request.UserSearchRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Users findByNameMakeFreshlyLoggedIn(String userName);

    Users signUp(Users user, String couponCode);

    Users activateCoupon(String userName, String couponCode);

    void leavePark(Long amusementParkId, String userName);

    Users enterPark(Long amusementParkId, String userName);

    Users getOnMachine(Long amusementParkId, Long machineId, String userName);

    void getOffMachine(Long amusementParkId, Long machineId, String userName);

    Page<UserResponseDto> findAll(UserSearchRequestDto dto, Pageable pageable);

    void modifyMoney(ModifyMoneyRequestDto dto);

    void makeCreator(String userName);

}
