package hu.beni.amusementpark.helper;

import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.beni.amusementpark.dto.request.MachineCreateRequestDto;
import hu.beni.amusementpark.dto.request.VisitorSignUpRequestDto;

import java.time.LocalDate;

public class ValidRequestDtoFactory {

    public static AmusementParkCreateRequestDto createAmusementPark() {
        return AmusementParkCreateRequestDto
                .builder()
                .name("Bence's park")
                .capital(3000)
                .totalArea(1000)
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
                .email("dto@gmail.com")
                .password(StringParamConstants.VALID_PASSWORD)
                .confirmPassword(StringParamConstants.VALID_PASSWORD)
                .dateOfBirth(LocalDate.of(1994, 10, 22))
                .photo("asdfghjkl").build();
    }
}