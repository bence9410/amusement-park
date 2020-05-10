package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class AmusementParkPageObject extends GuestBookPageObject {
	
	private final DriverFacade driverFacade;
	
	public AmusementParkPageObject(DriverFacade driverFacade, String email, String photo) {
		super(driverFacade);
		this.driverFacade=driverFacade;
		driverFacade.text("#email", email);
		driverFacade.visible("#photo").getAttribute("src").equals(photo);
	}
	
	public void createButtonHidden(){
		driverFacade.notPresent("#amusementParkShowCreateButton");
	}
	
	public int getSpendingMoney() {
		return Integer.parseInt(driverFacade.visible("#spendingMoney").getText());
		
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
	
	public void searchButtonClick() {
		driverFacade.click("#amusementParkShowSearchButton");
	}
	
	public void search(String name, int capitalMin, int capitalMax, int totalAreaMin, int totalAreaMax,
			int entranceFeeMin, int entranceFeeMax, int numberOfRows) {
		clearSearch();
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
	
	private void clearSearch() {
		driverFacade.deleteText("#searchName");
		driverFacade.deleteText("#searchCapitalMin");
		driverFacade.deleteText("#searchCapitalMax");
		driverFacade.deleteText("#searchTotalAreaMin");
		driverFacade.deleteText("#searchTotalAreaMax");
		driverFacade.deleteText("#searchEntranceFeeMin");
		driverFacade.deleteText("#searchEntranceFeeMax");

	}
	
	public void tableBodyFirstClick() {
		driverFacade.click("#tableBody td");
	}
	
	public MachinePageObject enterPark() {
		driverFacade.click("#enterPark");
		return  new MachinePageObject(driverFacade);
	}
	
	public void create(String parkName, int capital, int totalArea, int entanceFee) {
		driverFacade.click("#amusementParkShowCreateButton");
		driverFacade.write("#createAmusementParkName", "" + parkName);
		driverFacade.write("#createAmusementParkCapital", "" + capital);
		driverFacade.write("#createAmusementParkTotalArea", "" + totalArea);
		driverFacade.write("#createAmusementParkEntranceFee", "" + entanceFee);
		driverFacade.click("#createAmusementParkButton");
		driverFacade.hidden(".modal-backdrop");
	}

	public void scrollAndNumberOfPage(String page, String pageNumber) {
		driverFacade.click("#"+page);
		driverFacade.text("#numberOfPage", pageNumber);
	}
	
	public void numberOfPage(String pageNumber) {
		driverFacade.text("#numberOfPage", pageNumber);
	}
	
	public void detailClose() {
		driverFacade.click("#amusementParkDetails button");
		driverFacade.hidden(".modal-backdrop");
	}
	
	public LoginAndSignUpPageObject logout() {
		driverFacade.click("#logout");
		return new LoginAndSignUpPageObject(driverFacade);
	}
}
