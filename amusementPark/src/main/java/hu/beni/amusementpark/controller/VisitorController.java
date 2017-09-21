package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.entity.Visitor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static hu.beni.amusementpark.constants.MappingConstants.*;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "amusementPark/{amusementParkId}")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/visitor")
    public Resource<Visitor> enterPark(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId, @RequestBody Visitor visitor) {
        return createResourceForRestVisitor(amusementParkId, visitorService.enterPark(amusementParkId, visitor));
    }

    @GetMapping("/visitor/{visitorId}")
    public Resource<Visitor> read(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId, @PathVariable(name = VISITOR_ID) Long visitorId) {
        return createResourceForRestVisitor(amusementParkId, visitorService.read(visitorId));
    }

    @DeleteMapping("/visitor/{visitorId}")
    public void leavePark(@PathVariable(name = VISITOR_ID) Long visitorId) {
        visitorService.leavePark(visitorId);
    }

    @PutMapping("/machine/{machineId}/visitor/{visitorId}/getOnMachine")
    public Resource<Visitor> getOnMachine(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId,
            @PathVariable(name = MACHINE_ID) Long machineId, @PathVariable(name = VISITOR_ID) Long visitorId) {
        return createResourceForOnMachineVisitor(amusementParkId, machineId, visitorService.getOnMachine(amusementParkId, machineId, visitorId));    
    }

    @PutMapping("/machine/{machineId}/visitor/{visitorId}/getOffMachine")
    public Resource<Visitor> getOffMachine(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId,
            @PathVariable(name = MACHINE_ID) Long machineId, @PathVariable(name = VISITOR_ID) Long visitorId) {
        return createResourceForRestVisitor(amusementParkId, visitorService.getOffMachine(machineId, visitorId));
    }

    private Resource<Visitor> createResourceForRestVisitor(Long amusementParkId, Visitor visitor){
    	Resource<Visitor> visitorResource = createResource(amusementParkId, visitor);
    	visitorResource.add(linkTo(methodOn(GuestBookController.class).writeInGuestBook(amusementParkId, visitor.getId(), null)).withRel(WRITE_IN_GUEST_BOOK));
    	return visitorResource;
    }
    
    private Resource<Visitor> createResourceForOnMachineVisitor(Long amusementParkId, Long machineId, Visitor visitor){
    	Resource<Visitor> visitorResource = createResource(amusementParkId, visitor);
    	visitorResource.add(linkTo(methodOn(getClass()).getOffMachine(amusementParkId, machineId, visitor.getId())).withRel(GET_OFF_MACHINE));
    	return visitorResource;
    }
    
    private Resource<Visitor> createResource(Long amusementParkId, Visitor visitor) {
        return new Resource<>(visitor, linkTo(methodOn(VisitorController.class).read(amusementParkId, visitor.getId())).withSelfRel());
    }
    
}
