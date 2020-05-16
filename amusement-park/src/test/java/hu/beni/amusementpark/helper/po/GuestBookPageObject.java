package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class GuestBookPageObject {

	protected final DriverFacade driverFacade;

	protected GuestBookPageObject(DriverFacade driverFacade) {
		this.driverFacade = driverFacade;
	}

	public void guestBookSearch(String timestampMin, String timestampMax, String visitor, String text,
			int guestBookTableRows) {
		driverFacade.write("#guestBookSearchTimestampMin", timestampMin);
		driverFacade.write("#guestBookSearchTimestampMax", timestampMax);
		driverFacade.write("#guestBookSearchVisitorEmail", visitor);
		driverFacade.write("#guestBookSearchText", text);
		driverFacade.click("#guestBookSearchButton");
		clearGuestBookSearch();
		driverFacade.numberOfRowsInTable("#guestBookTable", guestBookTableRows);
	}

	private void clearGuestBookSearch() {
		driverFacade.deleteText("#guestBookSearchTimestampMin");
		driverFacade.deleteText("#guestBookSearchTimestampMax");
		driverFacade.deleteText("#guestBookSearchVisitorEmail");
		driverFacade.deleteText("#guestBookSearchText");

	}

	public void guestBookNumberOfPage(String pageNumber) {
		driverFacade.text("#guestBookNumberOfPage", pageNumber);
	}

	public void guestBookScrollAndNumberOfPage(String page, String pageNumber) {
		driverFacade.click("#guestBook" + page);
		driverFacade.text("#guestBookNumberOfPage", pageNumber);
	}

	public void guestBookTableNumberOfRows(int rows) {
		driverFacade.numberOfRowsInTable("#guestBookTable", rows);
	}
}
