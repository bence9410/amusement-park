package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class MessagePageObject {

    private final DriverFacade driverFacade;

    public MessagePageObject(DriverFacade driverFacade) {
        this.driverFacade = driverFacade;
    }

    public void messagesShowAndClickHide(String text) {
        driverFacade.text(".v-alert__content", text);
        driverFacade.click(".v-alert__content");
        driverFacade.notPresent(".v-alert__content");
    }
}
