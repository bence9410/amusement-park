package hu.beni.tester.resource;

import org.springframework.hateoas.ResourceSupport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MachineResource extends ResourceSupport {

	private Long identifier;

	private String fantasyName;

	private Integer size;

	private Integer price;

	private Integer numberOfSeats;

	private Integer minimumRequiredAge;

	private Integer ticketPrice;

	private String type;

}
