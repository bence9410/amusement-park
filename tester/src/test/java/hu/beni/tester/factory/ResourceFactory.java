package hu.beni.tester.factory;

import hu.beni.tester.properties.AmusementParkDataProperties;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.DataProperties;
import hu.beni.tester.properties.MachineDataProperties;
import hu.beni.tester.resource.AmusementParkResource;
import hu.beni.tester.resource.MachineResource;
import hu.beni.tester.resource.VisitorResource;
import org.springframework.stereotype.Component;

import static hu.beni.tester.constants.Constants.DATE_OF_BIRTH;
import static hu.beni.tester.constants.Constants.VALID_PASSWORD;

@Component
public class ResourceFactory {

    private final AmusementParkDataProperties amusementPark;
    private final MachineDataProperties machine;

    public ResourceFactory(ApplicationProperties applicationProperties) {
        DataProperties data = applicationProperties.getData();
        amusementPark = data.getAmusementPark();
        machine = data.getMachine();
    }

    public AmusementParkResource createAmusementPark() {
        return AmusementParkResource
                .builder() //@formatter:off
				.name("Beni parkja")
				.capital(amusementPark.getCapital())
				.totalArea(1000)
				.entranceFee(amusementPark.getEntranceFee()).build(); //@formatter:on
    }

    public MachineResource createMachine() {
        return MachineResource
                .builder() //@formatter:off
				.fantasyName("Nagy hajó")
				.size(100)
				.price(machine.getPrice())
				.numberOfSeats(250)
				.minimumRequiredAge(18)
				.ticketPrice(machine.getTicketPrice())
				.type("CAROUSEL").build(); //@formatter:on
    }

    public VisitorResource createVisitor(String email) {
        return VisitorResource
                .builder() //@formatter:off
				.email(email)
				.password(VALID_PASSWORD)
				.confirmPassword(VALID_PASSWORD)
				.dateOfBirth(DATE_OF_BIRTH).build(); //@formatter:on
    }
}