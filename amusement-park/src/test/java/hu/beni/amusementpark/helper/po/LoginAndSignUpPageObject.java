package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class LoginAndSignUpPageObject {

	private final DriverFacade driverFacade;
	
	public LoginAndSignUpPageObject(DriverFacade driverFacade) {
		this.driverFacade=driverFacade;
		driverFacade.hidden("#header");
		driverFacade.text("h1", "Welcome Visitor");
		driverFacade.text("[for=loginEmail]","Email:");
		driverFacade.text("[for=password]","Password:");
		driverFacade.visible("#loginEmail");
		driverFacade.visible("#password");
		driverFacade.visible("#login");
		driverFacade.visible("#showSignUpButton");
		driverFacade.hidden("#signUpDiv");
		
	}
	
	public AmusementParkPageObject signUp(String email, String password,String dateOfBirth,String photo) {
		driverFacade.click("#showSignUpButton");
		driverFacade.write("#signUpLoginEmail", email);
		driverFacade.write("#signUpPassword", password);
		driverFacade.write("#signUpConfirmPassword", password);
		driverFacade.write("#dateOfBirth",dateOfBirth );
		driverFacade.setAttribute("img", "src", photo);
		driverFacade.click("#signUpButton");
		return new AmusementParkPageObject(driverFacade,email,photo);
	}
	
}
