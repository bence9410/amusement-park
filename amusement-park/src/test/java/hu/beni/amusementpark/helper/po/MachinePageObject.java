package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.helper.DriverFacade;

public class MachinePageObject extends GuestBookPageObject {

    public MachinePageObject(DriverFacade driverFacade) {
        super(driverFacade);
        driverFacade.visible("#leave");
    }

    public void createButtonHidden() {
        driverFacade.notPresent("#machineShowCreateButton");
    }

    public void tableNumberOfRows(int rows) {
        driverFacade.numberOfRowsInTable("#tableBody", rows);
    }

    public void numberOfPage(String page) {
        driverFacade.text("#numberOfPage", page);
    }

    public void scrollAndNumberOfPage(String page, String pageNumber) {
        driverFacade.click("#" + page);
        driverFacade.text("#numberOfPage", pageNumber);
    }

    public void getOnAndOffMachine(int numberOfRow) {
        driverFacade.click("#tableBody tr:nth-child(" + numberOfRow + ") input");
        driverFacade.sleep(1);
        driverFacade.click("#machineModalExit");
        driverFacade.hidden(".modal-backdrop");

    }

    public void guestBookWrite(String textOfRegistry) {
        driverFacade.click("#guestBookButton");
        driverFacade.write("#guestBookText", textOfRegistry);
        driverFacade.click("#guestBookSave");
        driverFacade.click("#guestBookModal button");
        driverFacade.hidden(".modal-backdrop");

    }

    public void guestBookButtonClick() {
        driverFacade.click("#guestBookButton");
    }

    public void guestBookModalClose() {
        driverFacade.click("#guestBookModal button");
        driverFacade.hidden(".modal-backdrop");
    }

    public void create(String name, int size, int price, int numberOfSeats, int minimumRequiredAge, int ticketPrice,
                       MachineType type) {
        driverFacade.click("#machineShowCreateButton");
        driverFacade.write("#machineCreateFantasyName", name);
        driverFacade.write("#machineCreateSize", Integer.toString(size));
        driverFacade.write("#machineCreatePrice", Integer.toString(price));
        driverFacade.write("#machineCreateNumberOfSeats", Integer.toString(numberOfSeats));
        driverFacade.write("#machineCreateMinimumRequiredAge", Integer.toString(minimumRequiredAge));
        driverFacade.write("#machineCreateTicketPrice", Integer.toString(ticketPrice));
        driverFacade.select("#machineCreateType", type.toString());
        driverFacade.click("#machineCreateButton");
        driverFacade.hidden(".modal-backdrop");

    }

    public void searchButtonClick() {
        driverFacade.click("#showMachineSearch");
    }

    public void search(String name, int sizeMin, int sizeMax, int priceMin, int priceMax, int numberOfSeatsMin,
                       int numberOfSeatsMax, int minimumRequiredAgeMin, int minimumRequiredAgeMax, int ticketPriceMin,
                       int ticketPriceMax, String type, int rows) {
        driverFacade.write("#machineSearchFantasyName", name);
        driverFacade.write("#machineSearchSizeMin", Integer.toString(sizeMin));
        driverFacade.write("#machineSearchSizeMax", Integer.toString(sizeMax));
        driverFacade.write("#machineSearchPriceMin", Integer.toString(priceMin));
        driverFacade.write("#machineSearchPriceMax", Integer.toString(priceMax));
        driverFacade.write("#machineSearchNumberOfSeatsMin", Integer.toString(numberOfSeatsMin));
        driverFacade.write("#machineSearchNumberOfSeatsMax", Integer.toString(numberOfSeatsMax));
        driverFacade.write("#machineSearchMinimumRequiredAgeMin", Integer.toString(minimumRequiredAgeMin));
        driverFacade.write("#machineSearchMinimumRequiredAgeMax", Integer.toString(minimumRequiredAgeMax));
        driverFacade.write("#machineSearchTicketPriceMin", Integer.toString(ticketPriceMin));
        driverFacade.write("#machineSearchTicketPriceMax", Integer.toString(ticketPriceMax));
        driverFacade.select("#machineSearchType", type);
        driverFacade.click("#machineSearchButton");
        driverFacade.numberOfRowsInTable("#tableBody", rows);
    }

    public void clearSearch() {
        driverFacade.deleteText("#machineSearchFantasyName");
        driverFacade.deleteText("#machineSearchSizeMin");
        driverFacade.deleteText("#machineSearchSizeMax");
        driverFacade.deleteText("#machineSearchPriceMin");
        driverFacade.deleteText("#machineSearchPriceMax");
        driverFacade.deleteText("#machineSearchNumberOfSeatsMin");
        driverFacade.deleteText("#machineSearchNumberOfSeatsMax");
        driverFacade.deleteText("#machineSearchMinimumRequiredAgeMin");
        driverFacade.deleteText("#machineSearchMinimumRequiredAgeMax");
        driverFacade.deleteText("#machineSearchTicketPriceMin");
        driverFacade.deleteText("#machineSearchTicketPriceMax");
        driverFacade.select("#machineSearchType", "");
    }

    public void leave() {
        driverFacade.click("#leave");
    }
}
