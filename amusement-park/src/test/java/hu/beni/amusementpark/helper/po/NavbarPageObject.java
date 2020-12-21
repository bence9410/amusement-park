package hu.beni.amusementpark.helper.po;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.DriverFacade;

public class NavbarPageObject {

    private final DriverFacade driverFacade;

    public NavbarPageObject(DriverFacade driverFacade) {
        this.driverFacade = driverFacade;
    }

    public void navbarCorrectPersonControl(Visitor visitor) {
        driverFacade.waitUntil(driver -> {
            return driver.findElement(By.cssSelector(".v-avatar .v-image__image")).getAttribute("style")
                    .contains(visitor.getPhoto().getPhoto());

        });
        driverFacade.text("#email", visitor.getEmail());
        driverFacade.text("#spendingMoney", visitor.getSpendingMoney().toString());
        driverFacade.waitUntil(driver -> {
            List<WebElement> buttons = driver.findElements(By.cssSelector(".v-toolbar__content>button"));
            return buttons.size() == (visitor.getAuthority().equals("ROLE_ADMIN") ? 5 : 4) ? buttons : null;

        });
    }
}
