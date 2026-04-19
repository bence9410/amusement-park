package hu.beni.amusementpark.helper;

import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.MachineType;

import java.time.LocalDate;

public class ValidEntityFactory {

    public static AmusementPark createAmusementPark() {
        AmusementPark amusementPark = new AmusementPark();
        amusementPark.setName("Bence's park");
        amusementPark.setCapital(3000);
        amusementPark.setTotalArea(1000);
        amusementPark.setEntranceFee(50);
        return amusementPark;
    }

    public static Machine createMachine() {
        Machine machine = new Machine();
        machine.setFantasyName("Big ship");
        machine.setSize(100);
        machine.setPrice(250);
        machine.setNumberOfSeats(30);
        machine.setMinimumRequiredAge(18);
        machine.setTicketPrice(10);
        machine.setType(MachineType.SHIP);
        return machine;
    }

    public static Visitor createVisitor() {
        Visitor visitor = new Visitor();
        visitor.setEmail("entity@gmail.com");
        visitor.setPassword(StringParamConstants.VALID_PASSWORD);
        visitor.setAuthority("ROLE_ADMIN");
        visitor.setDateOfBirth(LocalDate.of(1994, 10, 22));
        visitor.setSpendingMoney(1000);
        return visitor;
    }

}
