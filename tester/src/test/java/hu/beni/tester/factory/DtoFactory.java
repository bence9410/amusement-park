package hu.beni.tester.factory;

import hu.beni.tester.properties.AmusementParkDataProperties;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.DataProperties;
import hu.beni.tester.properties.MachineDataProperties;
import hu.beni.tester.dto.AmusementParkDto;
import hu.beni.tester.dto.MachineDto;
import hu.beni.tester.dto.UserDto;
import org.springframework.stereotype.Component;

import static hu.beni.tester.constants.Constants.DATE_OF_BIRTH;
import static hu.beni.tester.constants.Constants.VALID_PASSWORD;

@Component
public class DtoFactory {

    private final AmusementParkDataProperties amusementPark;
    private final MachineDataProperties machine;

    public DtoFactory(ApplicationProperties applicationProperties) {
        DataProperties data = applicationProperties.getData();
        amusementPark = data.getAmusementPark();
        machine = data.getMachine();
    }

    public AmusementParkDto createAmusementPark() {
        return AmusementParkDto
                .builder()
                .name("Bence's park")
                .entranceFee(amusementPark.getEntranceFee()).build();
    }

    public MachineDto createMachine() {
        return MachineDto
                .builder()
                .fantasyName("Big ship")
                .minimumRequiredAge(18)
                .ticketPrice(machine.getTicketPrice())
                .video("asd")
                .videoLengthInSeconds(5).build();
    }

    public UserDto createVisitor(String name) {
        return UserDto
                .builder()
                .name(name)
                .password(VALID_PASSWORD)
                .confirmPassword(VALID_PASSWORD)
                .dateOfBirth(DATE_OF_BIRTH)
                .photo("asdfghjkl").build();
    }
}