package hu.beni.amusementpark.helper;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;

import java.time.LocalDate;

import static hu.beni.amusementpark.constants.StringParamConstants.EMAIL;
import static hu.beni.amusementpark.constants.StringParamConstants.VALID_PASSWORD;

public class ValidEntityFactory {

    public static AmusementPark createAmusementPark() {
        return AmusementPark.builder()
                .name("Bence's park")
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
                .email(EMAIL + "m")
                .password(VALID_PASSWORD)
                .authority("ROLE_ADMIN")
                .dateOfBirth(LocalDate.of(1994, 10, 22))
                .money(1000)
                .coupon(0).build();
    }

}
