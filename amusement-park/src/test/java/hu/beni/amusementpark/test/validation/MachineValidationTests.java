package hu.beni.amusementpark.test.validation;

import hu.beni.amusementpark.dto.resource.MachineResource;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.helper.ValidResourceFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Stream;

import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_26_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.*;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static java.util.stream.Collectors.toSet;

public class MachineValidationTests extends AbstractValidation {

    private static final String FANTASY_NAME = "fantasyName";
    private static final String SIZE = "size";
    private static final String PRICE = "price";
    private static final String NUMBER_OF_SEATS = "numberOfSeats";
    private static final String MINIMUM_REQUIRED_AGE = "minimumRequiredAge";
    private static final String TICKET_PRICE = "ticketPrice";
    private static final String TYPE = "type";

    private Machine machine;

    private MachineResource machineResource;

    @Before
    public void setUp() {
        machine = createMachine();

        machineResource = ValidResourceFactory.createMachine();
    }

    @Test
    public void validAddress() {
        validateAndAssertNoViolations(machine);

        validateAndAssertNoViolations(machineResource);
    }

    @Test
    public void invalidFantasyName() {
        String fantasyName = null;

        machine.setFantasyName(fantasyName);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getFantasyName(), FANTASY_NAME,
                NOT_NULL_MESSAGE);

        machineResource.setFantasyName(fantasyName);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getFantasyName(),
                FANTASY_NAME, NOT_NULL_MESSAGE);

        fantasyName = STRING_WITH_4_LENGTH;

        machine.setFantasyName(fantasyName);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getFantasyName(), FANTASY_NAME,
                sizeMessage(5, 25));

        machineResource.setFantasyName(fantasyName);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getFantasyName(),
                FANTASY_NAME, sizeMessage(5, 25));

        fantasyName = STRING_WITH_26_LENGTH;

        machine.setFantasyName(fantasyName);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getFantasyName(), FANTASY_NAME,
                sizeMessage(5, 25));

        machineResource.setFantasyName(fantasyName);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getFantasyName(),
                FANTASY_NAME, sizeMessage(5, 25));
    }

    @Test
    public void invalidSize() {
        Integer size = null;

        machine.setSize(size);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getSize(), SIZE, NOT_NULL_MESSAGE);

        machineResource.setSize(size);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getSize(), SIZE,
                NOT_NULL_MESSAGE);

        size = 19;

        machine.setSize(size);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getSize(), SIZE, rangeMessage(20, 750));

        machineResource.setSize(size);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getSize(), SIZE,
                rangeMessage(20, 200));

        size = 751;

        machine.setSize(size);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getSize(), SIZE, rangeMessage(20, 750));

        machineResource.setSize(size);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getSize(), SIZE,
                rangeMessage(20, 200));
    }

    @Test
    public void invalidPrice() {
        Integer price = null;

        machine.setPrice(price);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getPrice(), PRICE, NOT_NULL_MESSAGE);

        machineResource.setPrice(price);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getPrice(), PRICE,
                NOT_NULL_MESSAGE);

        price = 49;

        machine.setPrice(price);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getPrice(), PRICE, rangeMessage(50, 2000));

        machineResource.setPrice(price);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getPrice(), PRICE,
                rangeMessage(50, 2000));

        price = 2001;

        machine.setPrice(price);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getPrice(), PRICE, rangeMessage(50, 2000));

        machineResource.setPrice(price);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getPrice(), PRICE,
                rangeMessage(50, 2000));
    }

    @Test
    public void invalidNumberOfSeats() {
        Integer numberOfSeats = null;

        machine.setNumberOfSeats(numberOfSeats);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getNumberOfSeats(), NUMBER_OF_SEATS,
                NOT_NULL_MESSAGE);

        machineResource.setNumberOfSeats(numberOfSeats);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getNumberOfSeats(),
                NUMBER_OF_SEATS, NOT_NULL_MESSAGE);

        numberOfSeats = 4;

        machine.setNumberOfSeats(numberOfSeats);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getNumberOfSeats(), NUMBER_OF_SEATS,
                rangeMessage(5, 250));

        machineResource.setNumberOfSeats(numberOfSeats);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getNumberOfSeats(),
                NUMBER_OF_SEATS, rangeMessage(5, 250));

        numberOfSeats = 251;

        machine.setNumberOfSeats(numberOfSeats);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getNumberOfSeats(), NUMBER_OF_SEATS,
                rangeMessage(5, 250));

        machineResource.setNumberOfSeats(numberOfSeats);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getNumberOfSeats(),
                NUMBER_OF_SEATS, rangeMessage(5, 250));
    }

    @Test
    public void invalidMinimumRequiredAge() {
        Integer minimumRequiredAge = null;

        machine.setMinimumRequiredAge(minimumRequiredAge);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getMinimumRequiredAge(),
                MINIMUM_REQUIRED_AGE, NOT_NULL_MESSAGE);

        machineResource.setMinimumRequiredAge(minimumRequiredAge);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getMinimumRequiredAge(),
                MINIMUM_REQUIRED_AGE, NOT_NULL_MESSAGE);

        minimumRequiredAge = -1;

        machine.setMinimumRequiredAge(minimumRequiredAge);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getMinimumRequiredAge(),
                MINIMUM_REQUIRED_AGE, rangeMessage(0, 21));

        machineResource.setMinimumRequiredAge(minimumRequiredAge);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getMinimumRequiredAge(),
                MINIMUM_REQUIRED_AGE, rangeMessage(0, 21));

        minimumRequiredAge = 22;

        machine.setMinimumRequiredAge(minimumRequiredAge);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getMinimumRequiredAge(),
                MINIMUM_REQUIRED_AGE, rangeMessage(0, 21));

        machineResource.setMinimumRequiredAge(minimumRequiredAge);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getMinimumRequiredAge(),
                MINIMUM_REQUIRED_AGE, rangeMessage(0, 21));
    }

    @Test
    public void invalidTicketPrice() {
        Integer ticketPrice = null;

        machine.setTicketPrice(ticketPrice);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getTicketPrice(), TICKET_PRICE,
                NOT_NULL_MESSAGE);

        machineResource.setTicketPrice(ticketPrice);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getTicketPrice(),
                TICKET_PRICE, NOT_NULL_MESSAGE);

        ticketPrice = 4;

        machine.setTicketPrice(ticketPrice);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getTicketPrice(), TICKET_PRICE,
                rangeMessage(5, 30));

        machineResource.setTicketPrice(ticketPrice);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getTicketPrice(),
                TICKET_PRICE, rangeMessage(5, 30));

        ticketPrice = 31;

        machine.setTicketPrice(ticketPrice);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getTicketPrice(), TICKET_PRICE,
                rangeMessage(5, 30));

        machineResource.setTicketPrice(ticketPrice);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getTicketPrice(),
                TICKET_PRICE, rangeMessage(5, 30));
    }

    @Test
    public void invalidType() {
        machine.setType(null);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machine, machine.getType(), TYPE, NOT_NULL_MESSAGE);

        machineResource.setType(null);
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getType(), TYPE,
                NOT_NULL_MESSAGE);

        machineResource.setType("asd");
        validateAndAssertViolationsSizeIsOneAndViolationIs(machineResource, machineResource.getType(), TYPE,
                oneOfMessage(Stream.of(MachineType.values()).map(MachineType::toString).collect(toSet()).toString()));
    }
}