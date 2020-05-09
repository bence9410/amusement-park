package hu.beni.tester.resource;

import org.springframework.hateoas.RepresentationModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmusementParkResource extends RepresentationModel<AmusementParkResource> {

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

}
