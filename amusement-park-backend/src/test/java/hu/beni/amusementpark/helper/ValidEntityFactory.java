package hu.beni.amusementpark.helper;

import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;

import java.time.LocalDate;

public class ValidEntityFactory {

    public static AmusementPark createAmusementPark() {
        return AmusementPark.builder()
                .name("Bence's park")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50).build();
    }

    public static Machine createMachine() {
        return Machine.builder()
                .fantasyName("Big ship")
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .video("asd")
                .videoLengthInSeconds(5).build();
    }

    public static Visitor createVisitor() {
        return Visitor.builder()
                .email("entity@gmail.com")
                .password(StringParamConstants.VALID_PASSWORD)
                .authority("ROLE_ADMIN")
                .dateOfBirth(LocalDate.of(1994, 10, 22))
                .spendingMoney(1000).build();
    }

}
