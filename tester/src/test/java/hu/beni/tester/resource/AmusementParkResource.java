package hu.beni.tester.resource;

import org.springframework.hateoas.ResourceSupport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AmusementParkResource extends ResourceSupport {

	private Long identifier;

	private String name;

	private Integer capital;

	private Integer totalArea;

	private Integer entranceFee;

}
