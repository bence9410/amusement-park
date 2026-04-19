package hu.beni.amusementpark.helper;

import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.beni.amusementpark.dto.request.MachineCreateRequestDto;
import hu.beni.amusementpark.dto.request.VisitorSignUpRequestDto;

import java.time.LocalDate;

public class ValidRequestDtoFactory {

    public static MachineCreateRequestDto createMachine() {
        return MachineCreateRequestDto
                .builder()
                .fantasyName("Nagy hajó")
                .size(100)
                .price(250)
                .numberOfSeats(30)
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .type("CAROUSEL").build();
    }

    public static VisitorSignUpRequestDto createVisitor() {
        return VisitorSignUpRequestDto
                .builder()
                .email("resource@gmail.com")
                .password(StringParamConstants.VALID_PASSWORD)
                .confirmPassword(StringParamConstants.VALID_PASSWORD)
                .dateOfBirth(LocalDate.of(1994, 10, 22))
                .photo("asdfghjkl").build();
    }

    public static AmusementParkCreateRequestDto createAmusementPark() {
        return AmusementParkCreateRequestDto
                .builder()
                .name("Beni parkja")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50).build();
    }
}