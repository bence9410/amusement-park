package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class LoginPageObject {

    private final DriverFacade driverFacade;

    public LoginPageObject(DriverFacade driverFacade) {
        this.driverFacade = driverFacade;
    }

    public void attemptToLogin(String email, String password) {
        driverFacade.write(".v-input:nth-child(1) input", email);
        driverFacade.write(".v-input:nth-child(2) input", password);
        driverFacade.click("div .text-center>button");

    }

}
