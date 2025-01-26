package hu.beni.amusementpark.test.validation;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import org.junit.Before;
import org.junit.Test;

import static hu.beni.amusementpark.constants.StringParamConstants.*;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.sizeMessage;

public class GuestBookRegistryValidationTests extends AbstractValidation {

    private static final String TEXT_OF_REGISTRY = "textOfRegistry";
    private static final String AMUSEMENT_PARK = "amusementPark";
    private static final String VISITOR = "visitor";

    private GuestBookRegistry guestBookRegistry;

    @Before
    public void setUp() {
        guestBookRegistry = GuestBookRegistry.builder().textOfRegistry(OPINION_ON_THE_PARK)
                .visitor(Visitor.builder().email("benike@gmail.com").build())
                .amusementPark(AmusementPark.builder().id(1001L).build()).build();
    }

    @Test
    public void validAmusementPark() {
        validateAndAssertNoViolations(guestBookRegistry);
    }

    @Test
    public void invalidTextOfRegistry() {
        guestBookRegistry.setTextOfRegistry(null);
        validateAndAssertViolationsSizeIsOneAndViolationIs(guestBookRegistry, guestBookRegistry.getTextOfRegistry(),
                TEXT_OF_REGISTRY, NOT_NULL_MESSAGE);

        guestBookRegistry.setTextOfRegistry(STRING_WITH_1_LENGTH);
        validateAndAssertViolationsSizeIsOneAndViolationIs(guestBookRegistry, guestBookRegistry.getTextOfRegistry(),
                TEXT_OF_REGISTRY, sizeMessage(2, 100));

        guestBookRegistry.setTextOfRegistry(STRING_WITH_101_LENGTH);
        validateAndAssertViolationsSizeIsOneAndViolationIs(guestBookRegistry, guestBookRegistry.getTextOfRegistry(),
                TEXT_OF_REGISTRY, sizeMessage(2, 100));
    }

    @Test
    public void nullAmusementPark() {
        guestBookRegistry.setAmusementPark(null);
        validateAndAssertViolationsSizeIsOneAndViolationIs(guestBookRegistry, guestBookRegistry.getAmusementPark(),
                AMUSEMENT_PARK, NOT_NULL_MESSAGE);
    }

    @Test
    public void nullVisitor() {
        guestBookRegistry.setVisitor(null);
        validateAndAssertViolationsSizeIsOneAndViolationIs(guestBookRegistry, guestBookRegistry.getVisitor(), VISITOR,
                NOT_NULL_MESSAGE);
    }
}
