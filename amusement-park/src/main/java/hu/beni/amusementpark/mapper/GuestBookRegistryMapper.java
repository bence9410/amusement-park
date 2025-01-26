package hu.beni.amusementpark.mapper;

import hu.beni.amusementpark.controller.GuestBookRegistryController;
import hu.beni.amusementpark.dto.resource.GuestBookRegistryResource;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static hu.beni.amusementpark.factory.LinkFactory.createAddGuestBookRegistryLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGuestBookRegistrySelfLink;

@Component
@ConditionalOnWebApplication
public class GuestBookRegistryMapper extends EntityMapper<GuestBookRegistry, GuestBookRegistryResource> {

    public GuestBookRegistryMapper(PagedResourcesAssembler<GuestBookRegistry> pagedResourcesAssembler) {
        super(GuestBookRegistryController.class, GuestBookRegistryResource.class, pagedResourcesAssembler);
    }

    @Override
    public GuestBookRegistryResource toModel(GuestBookRegistry entity) {
        return GuestBookRegistryResource
                .builder() //@formatter:off
				.identifier(entity.getId())
				.textOfRegistry(entity.getTextOfRegistry())
				.dateOfRegistry(entity.getDateOfRegistry())
				.amusementParkId(entity.getAmusementPark().getId())
				.visitorEmail(entity.getVisitor().getEmail())
				.links(createLinks(entity)).build(); //@formatter:on
    }

    @Override
    public GuestBookRegistry toEntity(GuestBookRegistryResource resource) {
        return GuestBookRegistry
                .builder() //@formatter:off
				.id(resource.getIdentifier())
				.textOfRegistry(resource.getTextOfRegistry())
				.dateOfRegistry(resource.getDateOfRegistry()).build(); //@formatter:on
    }

    private Link[] createLinks(GuestBookRegistry guestBookRegistry) {
        return new Link[] { //@formatter:off
				createGuestBookRegistrySelfLink(guestBookRegistry.getId()),
				createAddGuestBookRegistryLink(null)}; //@formatter:on
    }
}
