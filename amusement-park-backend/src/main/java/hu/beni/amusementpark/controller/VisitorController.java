package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.constants.Constants;
import hu.beni.amusementpark.dto.request.VisitorSignUpRequestDto;
import hu.beni.amusementpark.dto.response.VisitorResponseDto;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
public class VisitorController {

    private final VisitorService visitorService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @GetMapping("/me")
    public ResponseEntity<VisitorResponseDto> me(Principal principal) {
        Optional<Visitor> visitor = Optional.ofNullable(principal).map(Principal::getName)
                .map(visitorService::findByEmailMakeFreshlyLoggedIn);
        return visitor.map(VisitorMapper::toDto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/login")
    public VisitorResponseDto login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("email");
        String password = request.getParameter("password");

        Optional.ofNullable(username).filter(email -> email.matches(Constants.EMAIL_REGEXP)).orElseThrow(
                () -> new BadCredentialsException("Email must be well-formed, for example: somebody@example.com"));

        saveSecurityContext(username, password, request, response);
        return VisitorMapper.toDto(visitorService.findByEmailMakeFreshlyLoggedIn(username));
    }

    @PostMapping("/signUp")
    public VisitorResponseDto signUp(@Valid @RequestBody VisitorSignUpRequestDto visitorSignUpRequestDto,
                                     HttpServletRequest request, HttpServletResponse response) {
        VisitorResponseDto visitorResponseDto = VisitorMapper
                .toDto(visitorService.signUp(VisitorMapper.toEntity(visitorSignUpRequestDto)));
        saveSecurityContext(visitorSignUpRequestDto.getEmail(), visitorSignUpRequestDto.getPassword(), request, response);
        return visitorResponseDto;
    }

    private void saveSecurityContext(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
    }

    @PostMapping("/visitors/uploadMoney")
    public void uploadMoney(@Range(min = 1) @RequestBody Integer amount, Principal principal) {
        visitorService.uploadMoney(amount, principal.getName());
    }

    @PutMapping("amusement-parks/{amusementParkId}/visitors/enter-park")
    public void enterPark(@PathVariable Long amusementParkId, Principal principal) {
        visitorService.enterPark(amusementParkId, principal.getName());
    }

    @PutMapping("amusement-parks/{amusementParkId}/visitors/leave-park")
    public void leavePark(@PathVariable Long amusementParkId, Principal principal) {
        visitorService.leavePark(amusementParkId, principal.getName());
    }

    @PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/visitors/get-on-machine")
    public void getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
                             Principal principal) {
        visitorService.getOnMachine(amusementParkId, machineId, principal.getName());
    }

    @PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/visitors/get-off-machine")
    public void getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
                              Principal principal) {
        visitorService.getOffMachine(amusementParkId, machineId, principal.getName());
    }
}
