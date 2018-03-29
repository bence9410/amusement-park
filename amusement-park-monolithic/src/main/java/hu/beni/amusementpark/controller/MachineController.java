package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.entity.Machine;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.*;
import hu.beni.amusementpark.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(path = "amusementPark/{amusementParkId}/machine")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Resource<Machine> addMachine(@PathVariable Long amusementParkId, @RequestBody Machine machine) {
        return createResource(amusementParkId, machineService.addMachine(amusementParkId, machine));
    }

    @GetMapping("/{machineId}")
    public Resource<Machine> findOne(@PathVariable Long amusementParkId, @PathVariable Long machineId) {
        return createResource(amusementParkId, machineService.findOne(machineId));
    }
    
    @GetMapping
    public List<Resource<Machine>> findAllByAmusementParkId(@PathVariable Long amusementParkId){
    	return machineService.findAllByAmusementParkId(amusementParkId).stream()
    			.map(machine -> createResource(amusementParkId, machine)).collect(Collectors.toList());
    }

    @DeleteMapping("/{machineId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long amusementParkId, @PathVariable Long machineId) {
        machineService.removeMachine(amusementParkId, machineId);
    }

    private Resource<Machine> createResource(Long amusementParkId, Machine machine) {
        return new Resource<>(machine, linkTo(methodOn(MachineController.class).findOne(amusementParkId, machine.getId())).withSelfRel(),
                linkTo(methodOn(VisitorController.class).getOnMachine(amusementParkId, machine.getId(), null)).withRel(GET_ON_MACHINE));
    }

}