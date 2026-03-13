package hu.beni.tester.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmusementParkResource extends RepresentationModel<AmusementParkResource> {

    private Long identifier;

    private String name;

    private Integer capital;

    private Integer totalArea;

    private Integer entranceFee;

}
