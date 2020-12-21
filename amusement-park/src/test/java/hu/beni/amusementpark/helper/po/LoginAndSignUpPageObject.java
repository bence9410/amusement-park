package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class LoginAndSignUpPageObject {

	private final DriverFacade driverFacade;

	public LoginAndSignUpPageObject(DriverFacade driverFacade) {
		this.driverFacade = driverFacade;
	}

	public AmusementParkPageObject signUp(String email, String password, String dateOfBirth, String photo) {
		driverFacade.click("div>a");
		driverFacade.write(".v-input:nth-child(1) input", email);
		driverFacade.write(".v-input:nth-child(2) input", password);
		driverFacade.write(".v-input:nth-child(3) input", password);
		driverFacade.click(".v-date-picker-years:nth-child(18)");
		driverFacade.setAttribute("img", "src", photo);
		driverFacade.click(".text-center>button");
		return new AmusementParkPageObject(driverFacade, email, photo);
	}

	public AmusementParkPageObject login(String email, String password, String photo) {
		driverFacade.write(".v-input:nth-child(1) input", email);
		driverFacade.write(".v-input:nth-child(2) input", password);
		driverFacade.click("div .text-center>button");
		return new AmusementParkPageObject(driverFacade, email, photo);
	}
}
