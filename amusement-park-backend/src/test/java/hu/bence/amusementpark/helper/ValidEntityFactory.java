package hu.bence.amusementpark.helper;

import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.entity.Users;

import java.time.LocalDate;

import static hu.bence.amusementpark.constants.StringParamConstants.EMAIL;
import static hu.bence.amusementpark.constants.StringParamConstants.VALID_PASSWORD;

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

    public static Users createUser() {
        return Users.builder()
                .email(EMAIL + "m")
                .password(VALID_PASSWORD)
                .authority("ROLE_ADMIN")
                .dateOfBirth(LocalDate.of(1994, 10, 22))
                .money(1000)
                .coupon(0).build();
    }

}
