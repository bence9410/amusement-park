package hu.beni.tester.properties;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("tester")
public class ApplicationProperties {

	private DataProperties data;

	private NumberOfProperties numberOf;

	@PostConstruct
	public void init() {
		int numberOfAmusementParks = numberOf.getAdmins() * numberOf.getAmusementParksPerAdmin();
		numberOf.setAmusementParks(numberOfAmusementParks);
		VisitorDataProperties visitorDataProperties = new VisitorDataProperties();
		visitorDataProperties.setSpendingMoney(numberOfAmusementParks * data.getAmusementPark().getEntranceFee()
				+ numberOfAmusementParks * numberOf.getMachinesPerPark() * data.getMachine().getTicketPrice());
		data.setVisitor(visitorDataProperties);
	}

}
