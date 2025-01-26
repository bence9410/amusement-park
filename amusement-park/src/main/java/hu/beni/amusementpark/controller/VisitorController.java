package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.factory.LinkFactory;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @GetMapping("/me")
    public ResponseEntity<VisitorResource> me(Principal principal) {
        Optional<Visitor> visitor = Optional.ofNullable(principal).map(Principal::getName)
                .map(visitorService::findByEmail);
        visitor.ifPresent(v -> visitorService.getOffMachineAndLeavePark(v.getEmail()));
        return visitor.map(visitorMapper::toModelWithPhoto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/signUp")
    public VisitorResource signUp(@Valid @RequestBody VisitorResource visitorResource) {
        VisitorResource responseVisitorResource = visitorMapper
                .toModelWithPhoto(visitorService.signUp(visitorMapper.toEntity(visitorResource)));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(visitorResource.getEmail(), visitorResource.getPassword()));
        return responseVisitorResource;
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
