package hu.bence.amusementpark.helper;

import hu.bence.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.bence.amusementpark.dto.request.MachineCreateRequestDto;
import hu.bence.amusementpark.dto.request.VisitorSignUpRequestDto;

import java.time.LocalDate;

import static hu.bence.amusementpark.constants.StringParamConstants.EMAIL;
import static hu.bence.amusementpark.constants.StringParamConstants.VALID_PASSWORD;

public class ValidRequestDtoFactory {

    public static AmusementParkCreateRequestDto createAmusementPark() {
        return AmusementParkCreateRequestDto
                .builder()
                .name("Bence's park")
                .entranceFee(50).build();
    }

    public static MachineCreateRequestDto createMachine() {
        return MachineCreateRequestDto
                .builder()
                .fantasyName("Big ship")
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .video("asd")
                .videoLengthInSeconds(5).build();
    }

    public static VisitorSignUpRequestDto createVisitor() {
        return VisitorSignUpRequestDto
                .builder()
                .email(EMAIL + "m")
                .password(VALID_PASSWORD)
                .confirmPassword(VALID_PASSWORD)
                .dateOfBirth(LocalDate.of(1994, 10, 22))
                .photo("asdfghjkl").build();
    }
}