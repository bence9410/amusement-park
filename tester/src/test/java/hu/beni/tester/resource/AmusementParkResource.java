package hu.beni.tester.resource;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
public class AmusementParkResource extends RepresentationModel<AmusementParkResource> {

    private Long identifier;

    private String name;

    private Integer capital;

    private Integer totalArea;

    private Integer entranceFee;

}
