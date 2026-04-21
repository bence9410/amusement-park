package hu.bence.amusementpark.controller;

import hu.bence.amusementpark.dto.request.UserSignUpRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.mapper.UserMapper;
import hu.bence.amusementpark.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(Principal principal) {
        Optional<Users> user = Optional.ofNullable(principal).map(Principal::getName)
                .map(userService::findByNameMakeFreshlyLoggedIn);
        return user.map(UserMapper::toDto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/login")
    public UserResponseDto login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("name");
        String password = request.getParameter("password");

        Optional.ofNullable(username).filter(name -> name.length() >= 3 && name.length() <= 50).orElseThrow(
                () -> new BadCredentialsException("Name length must be between 3 and 50."));

        saveSecurityContext(username, password, request, response);
        return UserMapper.toDto(userService.findByNameMakeFreshlyLoggedIn(username));
    }

    @PostMapping("/signUp")
    public UserResponseDto signUp(@Valid @RequestBody UserSignUpRequestDto userSignUpRequestDto,
                                  HttpServletRequest request, HttpServletResponse response) {
        UserResponseDto userResponseDto = UserMapper
                .toDto(userService.signUp(UserMapper.toEntity(userSignUpRequestDto), userSignUpRequestDto.getCouponCode()));
        saveSecurityContext(userSignUpRequestDto.getName(), userSignUpRequestDto.getPassword(), request, response);
        return userResponseDto;
    }

    private void saveSecurityContext(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
    }

    @PostMapping("/uploadMoney")
    public void uploadMoney(@Range(min = 1) @RequestBody Integer amount, Principal principal) {
        userService.uploadMoney(amount, principal.getName());
    }

    @PostMapping("/activate-coupon")
    public UserResponseDto activateCoupon(@NotNull @Size(min = 1) @RequestBody String couponCode, Principal principal) {
        return UserMapper.toDtoWithoutPhoto(userService.activateCoupon(principal.getName(), couponCode));
    }

    @PutMapping("amusement-parks/{amusementParkId}/enter-park")
    public UserResponseDto enterPark(@PathVariable Long amusementParkId, Principal principal) {
        return UserMapper.toDtoWithoutPhoto(userService.enterPark(amusementParkId, principal.getName()));
    }

    @PutMapping("amusement-parks/{amusementParkId}/leave-park")
    public void leavePark(@PathVariable Long amusementParkId, Principal principal) {
        userService.leavePark(amusementParkId, principal.getName());
    }

    @PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/get-on-machine")
    public UserResponseDto getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
                                        Principal principal) {
        return UserMapper.toDtoWithoutPhoto(userService.getOnMachine(amusementParkId, machineId, principal.getName()));
    }

    @PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/get-off-machine")
    public void getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
                              Principal principal) {
        userService.getOffMachine(amusementParkId, machineId, principal.getName());
    }
}
