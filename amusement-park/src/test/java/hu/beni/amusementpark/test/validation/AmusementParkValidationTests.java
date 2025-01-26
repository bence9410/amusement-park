package hu.beni.amusementpark.test.validation;

import hu.beni.amusementpark.dto.resource.AmusementParkResource;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.helper.ValidResourceFactory;
import org.junit.Before;
import org.junit.Test;

import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_21_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.*;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;

public class AmusementParkValidationTests extends AbstractValidation {

    private static final String NAME = "name";
    private static final String CAPITAL = "capital";
    private static final String TOTAL_AREA = "totalArea";
    private static final String ENTRANCE_FEE = "entranceFee";

    private AmusementPark amusementPark;

    private AmusementParkResource amusementParkResource;

    @Before
    public void setUp() {
        amusementPark = createAmusementPark();

        amusementParkResource = ValidResourceFactory.createAmusementPark();
    }

    @Test
    public void validAmusementPark() {
        validateAndAssertNoViolations(amusementPark);

        validateAndAssertNoViolations(amusementParkResource);
    }

    @Test
    public void invalidName() {
        String name = null;

        amusementPark.setName(name);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getName(), NAME,
                NOT_NULL_MESSAGE);

        amusementParkResource.setName(name);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getName(), NAME,
                NOT_NULL_MESSAGE);

        name = STRING_WITH_4_LENGTH;

        amusementPark.setName(name);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getName(), NAME,
                sizeMessage(5, 20));

        amusementParkResource.setName(name);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getName(), NAME,
                sizeMessage(5, 20));

        name = STRING_WITH_21_LENGTH;

        amusementPark.setName(name);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getName(), NAME,
                sizeMessage(5, 20));

        amusementParkResource.setName(name);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getName(), NAME,
                sizeMessage(5, 20));
    }

    @Test
    public void invalidCapital() {
        Integer capital = null;

        amusementPark.setCapital(capital);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getCapital(), CAPITAL,
                NOT_NULL_MESSAGE);

        amusementParkResource.setCapital(capital);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getCapital(),
                CAPITAL, NOT_NULL_MESSAGE);

        capital = 99;

        amusementPark.setCapital(capital);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getCapital(), CAPITAL,
                rangeMessage(500, 50000));

        amusementParkResource.setCapital(capital);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getCapital(),
                CAPITAL, rangeMessage(500, 50000));

        capital = 50001;

        amusementPark.setCapital(capital);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getCapital(), CAPITAL,
                rangeMessage(500, 50000));

        amusementParkResource.setCapital(capital);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getCapital(),
                CAPITAL, rangeMessage(500, 50000));
    }

    @Test
    public void invalidTotalArea() {
        Integer totalArea = null;

        amusementPark.setTotalArea(totalArea);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getTotalArea(), TOTAL_AREA,
                NOT_NULL_MESSAGE);

        amusementParkResource.setTotalArea(totalArea);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getTotalArea(),
                TOTAL_AREA, NOT_NULL_MESSAGE);

        totalArea = 49;

        amusementPark.setTotalArea(totalArea);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getTotalArea(), TOTAL_AREA,
                rangeMessage(50, 5000));

        amusementParkResource.setTotalArea(totalArea);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getTotalArea(),
                TOTAL_AREA, rangeMessage(50, 5000));

        totalArea = 5001;

        amusementPark.setTotalArea(totalArea);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getTotalArea(), TOTAL_AREA,
                rangeMessage(50, 5000));

        amusementParkResource.setTotalArea(totalArea);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource, amusementParkResource.getTotalArea(),
                TOTAL_AREA, rangeMessage(50, 5000));
    }

    @Test
    public void invalidEntranceFee() {
        Integer entranceFee = null;

        amusementPark.setEntranceFee(entranceFee);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getEntranceFee(), ENTRANCE_FEE,
                NOT_NULL_MESSAGE);

        amusementParkResource.setEntranceFee(entranceFee);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource,
                amusementParkResource.getEntranceFee(), ENTRANCE_FEE, NOT_NULL_MESSAGE);

        entranceFee = 4;

        amusementPark.setEntranceFee(entranceFee);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getEntranceFee(), ENTRANCE_FEE,
                rangeMessage(5, 200));

        amusementParkResource.setEntranceFee(entranceFee);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource,
                amusementParkResource.getEntranceFee(), ENTRANCE_FEE, rangeMessage(5, 200));

        entranceFee = 201;

        amusementPark.setEntranceFee(entranceFee);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementPark, amusementPark.getEntranceFee(), ENTRANCE_FEE,
                rangeMessage(5, 200));

        amusementParkResource.setEntranceFee(entranceFee);
        validateAndAssertViolationsSizeIsOneAndViolationIs(amusementParkResource,
                amusementParkResource.getEntranceFee(), ENTRANCE_FEE, rangeMessage(5, 200));
    }
}