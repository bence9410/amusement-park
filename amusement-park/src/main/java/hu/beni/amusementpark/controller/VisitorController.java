package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.constants.Constants;
import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.factory.LinkFactory;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final VisitorMapper visitorMapper;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @GetMapping("/me")
    public ResponseEntity<VisitorResource> me(Principal principal) {
        Optional<Visitor> visitor = Optional.ofNullable(principal).map(Principal::getName)
                .map(visitorService::findByEmail);
        visitor.ifPresent(v -> visitorService.getOffMachineAndLeavePark(v.getEmail()));
        return visitor.map(visitorMapper::toModelWithPhoto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/login")
    public VisitorResource login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("email");
        String password = request.getParameter("password");

        Optional.ofNullable(username).filter(email -> email.matches(Constants.EMAIL_REGEXP)).orElseThrow(
                () -> new BadCredentialsException("Email must be well-formed, for example: somebody@example.com"));

        saveSecurityContext(username, password, request, response);
        visitorService.getOffMachineAndLeavePark(username);
        return visitorMapper.toModelWithPhoto(visitorService.findByEmail(username));
    }

    @PostMapping("/signUp")
    public VisitorResource signUp(@Valid @RequestBody VisitorResource visitorResource, HttpServletRequest request, HttpServletResponse response) {
        VisitorResource responseVisitorResource = visitorMapper
                .toModelWithPhoto(visitorService.signUp(visitorMapper.toEntity(visitorResource)));
        saveSecurityContext(visitorResource.getEmail(), visitorResource.getPassword(), request, response);
        return responseVisitorResource;
    }

    private void saveSecurityContext(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication = authenticationManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        securityContextRepository.saveContext(securityContext, request, response);
    }

    @PostMapping("/visitors/uploadMoney")
    public ResponseEntity<Void> uploadMoney(@Range(min = 1) @RequestBody Integer amount, Principal principal) {
        visitorService.uploadMoney(amount, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/visitors")
    public CollectionModel<VisitorResource> findAllVisitor() {
        CollectionModel<VisitorResource> resources = visitorMapper.toCollectionModel(visitorService.findAllVisitor());
        resources.forEach(pr -> {
            pr.removeLinks();
            pr.add(LinkFactory.createVisitorLinkWithSelfRel(pr.getEmail()));
        });
        return resources;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/visitors/{visitorEmail}")
    public VisitorResource findByEmail(@PathVariable String visitorEmail) {
        return visitorMapper.toModel(visitorService.findByEmail(visitorEmail));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/visitors/{visitorEmail}")
    public void delete(@PathVariable String visitorEmail) {
        visitorService.delete(visitorEmail);
    }

    @PutMapping("amusement-parks/{amusementParkId}/visitors/enter-park")
    public VisitorResource enterPark(@PathVariable Long amusementParkId, Principal principal) {
        return visitorMapper.toModel(visitorService.enterPark(amusementParkId, principal.getName()));
    }

    @PutMapping("amusement-parks/{amusementParkId}/visitors/leave-park")
    public VisitorResource leavePark(@PathVariable Long amusementParkId, Principal principal) {
        return visitorMapper.toModel(visitorService.leavePark(amusementParkId, principal.getName()));
    }

    @PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/visitors/get-on-machine")
    public VisitorResource getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
                                        Principal principal) {
        return visitorMapper.toModel(visitorService.getOnMachine(amusementParkId, machineId, principal.getName()));
    }

    @PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/visitors/get-off-machine")
    public VisitorResource getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
                                         Principal principal) {
        return visitorMapper.toModel(visitorService.getOffMachine(amusementParkId, machineId, principal.getName()));
    }
}
