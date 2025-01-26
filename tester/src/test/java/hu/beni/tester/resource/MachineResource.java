package hu.beni.tester.resource;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
public class MachineResource extends RepresentationModel<MachineResource> {

    private Long identifier;

    private String fantasyName;

    private Integer size;

    private Integer price;

    private Integer numberOfSeats;

    private Integer minimumRequiredAge;

    private Integer ticketPrice;

    private String type;

}
