package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class AmusementParkPageObject {
	
	private final DriverFacade driverFacade;
	
	public AmusementParkPageObject(DriverFacade driverFacade, String email, String photo) {
		this.driverFacade=driverFacade;
		driverFacade.text("#email", email);
		driverFacade.visible("#photo").getAttribute("src").equals(photo);
	}
	
	public void amusementParkCreateButtonHidden(){
		driverFacade.notPresent("#amusementParkShowCreateButton");
	}
	
	public void spendingMoneyTest(int money) {
		driverFacade.text("#spendingMoney", Integer.toString(money));
	}
	
	public void uploadMoney(int money) {
		driverFacade.click("#uploadMoney");
		driverFacade.write("#money", Integer.toString(money));
		driverFacade.click("#upload");
		driverFacade.click("#closeUpload");
		driverFacade.hidden(".modal-backdrop");
		
	}
	
	public void amusementParkSearchButtonClick() {
		driverFacade.click("#amusementParkShowSearchButton");
	}
	
	public void amusementParkSearch(String name, int capitalMin, int capitalMax, int totalAreaMin, int totalAreaMax,
			int entranceFeeMin, int entranceFeeMax, int numberOfRows) {
		amusementParkClearSearch();
		driverFacade.write("#searchName", name);
		driverFacade.write("#searchCapitalMin", Integer.toString(capitalMin));
		driverFacade.write("#searchCapitalMax", Integer.toString(capitalMax));
		driverFacade.write("#searchTotalAreaMin", Integer.toString(totalAreaMin));
		driverFacade.write("#searchTotalAreaMax", Integer.toString(totalAreaMax));
		driverFacade.write("#searchEntranceFeeMin", Integer.toString(entranceFeeMin));
		driverFacade.write("#searchEntranceFeeMax", Integer.toString(entranceFeeMax));
		driverFacade.click("#amusementParkSearchButton");
		driverFacade.numberOfRowsInTable("#tableBody", numberOfRows);
	}
	
	private void amusementParkClearSearch() {
		driverFacade.deleteText("#searchName");
		driverFacade.deleteText("#searchCapitalMin");
		driverFacade.deleteText("#searchCapitalMax");
		driverFacade.deleteText("#searchTotalAreaMin");
		driverFacade.deleteText("#searchTotalAreaMax");
		driverFacade.deleteText("#searchEntranceFeeMin");
		driverFacade.deleteText("#searchEntranceFeeMax");

	}
	
	public void amusementParkTableBodyFirstClick() {
		driverFacade.click("#tableBody td");
	}
	
	public MachinePageObject enterPark() {
		driverFacade.click("#enterPark");
		return  new MachinePageObject(driverFacade);
	}
	
	public LoginAndSignUpPageObject amusementParkLogout() {
		driverFacade.click("#logout");
		return new LoginAndSignUpPageObject(driverFacade);
	}

	
}
