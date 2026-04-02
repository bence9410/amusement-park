package hu.beni.amusementpark.helper;

import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.dto.resource.AmusementParkResource;
import hu.beni.amusementpark.dto.resource.MachineResource;
import hu.beni.amusementpark.dto.resource.VisitorResource;

import java.time.LocalDate;

public class ValidResourceFactory {

    private ValidResourceFactory() {
        super();
    }

    public static MachineResource createMachine() {
        return MachineResource
                .builder() //@formatter:off
				.fantasyName("Nagy hajó")
				.size(100)
				.price(250)
				.numberOfSeats(30)
				.minimumRequiredAge(18)
				.ticketPrice(10)
				.type("CAROUSEL").build(); //@formatter:on
    }

    public static VisitorResource createVisitor() {
        return VisitorResource
                .builder() //@formatter:off
				.email("resource@gmail.com")
    			.password(StringParamConstants.VALID_PASSWORD)
    			.confirmPassword(StringParamConstants.VALID_PASSWORD)
    			.dateOfBirth(LocalDate.of(1994, 10, 22)).build(); //@formatter:on
    }

    public static AmusementParkResource createAmusementPark() {
        return AmusementParkResource
                .builder() //@formatter:off
				.name("Beni parkja")
				.capital(3000)
				.totalArea(1000)
				.entranceFee(50).build(); //@formatter:on
    }
}