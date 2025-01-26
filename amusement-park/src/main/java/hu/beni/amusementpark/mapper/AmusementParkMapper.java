package hu.beni.amusementpark.mapper;

import hu.beni.amusementpark.controller.AmusementParkController;
import hu.beni.amusementpark.dto.resource.AmusementParkResource;
import hu.beni.amusementpark.entity.AmusementPark;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static hu.beni.amusementpark.factory.LinkFactory.*;

@Component
@ConditionalOnWebApplication
public class AmusementParkMapper extends EntityMapper<AmusementPark, AmusementParkResource> {

    public AmusementParkMapper(PagedResourcesAssembler<AmusementPark> pagedResourcesAssembler) {
        super(AmusementParkController.class, AmusementParkResource.class, pagedResourcesAssembler);
    }

    @Override
    public AmusementParkResource toModel(AmusementPark entity) {
        return AmusementParkResource
                .builder() //@formatter:off
				.identifier(entity.getId())
				.name(entity.getName())
				.capital(entity.getCapital())
				.totalArea(entity.getTotalArea())
				.entranceFee(entity.getEntranceFee())
				.links(createLinks(entity)).build(); //@formatter:on
    }

    @Override
    public AmusementPark toEntity(AmusementParkResource resource) {
        return AmusementPark
                .builder() //@formatter:off
				.id(resource.getIdentifier())
				.name(resource.getName())
				.capital(resource.getCapital())
				.totalArea(resource.getTotalArea())
				.entranceFee(resource.getEntranceFee()).build(); //@formatter:on
    }

    private Link[] createLinks(AmusementPark amusementPark) {
        Long amusementParkId = amusementPark.getId();
        return new Link[] { //@formatter:off
				createAmusementParkSelfLink(amusementParkId),
				createMachineLink(amusementParkId),
				createVisitorSignUpLink(),
				createVisitorEnterParkLink(amusementParkId)}; //@formatter:on
    }
}