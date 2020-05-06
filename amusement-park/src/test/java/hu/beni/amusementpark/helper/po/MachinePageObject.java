package hu.beni.amusementpark.helper.po;

import hu.beni.amusementpark.helper.DriverFacade;

public class MachinePageObject {

	private final DriverFacade driverFacade;
	
	public MachinePageObject(DriverFacade driverFacade) {
		this.driverFacade=driverFacade;
		driverFacade.visible("#leave");
	}
	
	public void machineCreateButtonHidden () {
		driverFacade.notPresent("#machineShowCreateButton");
	}
	public void machineTableNumberOfRows(int rows) {
		driverFacade.numberOfRowsInTable("#tableBody", rows);
	}
	
	public void getOnAndOffMachine(int numberOfRow) {
		driverFacade.click("#tableBody tr:nth-child(" + numberOfRow + ") input");
		driverFacade.click("#machineModalExit");
		driverFacade.hidden(".modal-backdrop");

	}
	
	public void machineLeave() {
		driverFacade.click("#leave");
	}
}
