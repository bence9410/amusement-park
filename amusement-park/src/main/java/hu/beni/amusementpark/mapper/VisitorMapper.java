package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.factory.LinkFactory.createAddGuestBookRegistryLink;
import static hu.beni.amusementpark.factory.LinkFactory.createAmusementParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOffMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOnMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMeLinkWithSelfRel;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorLeavePark;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.VisitorController;
import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Photo;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.factory.LinkFactory;

@Component
@ConditionalOnWebApplication
public class VisitorMapper extends EntityMapper<Visitor, VisitorResource> {

	private final PasswordEncoder passwordEncoder;

	public VisitorMapper(PagedResourcesAssembler<Visitor> pagedResourcesAssembler, PasswordEncoder passwordEncoder) {
		super(VisitorController.class, VisitorResource.class, pagedResourcesAssembler);
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public VisitorResource toModel(Visitor entity) {
		return VisitorResource
				.builder() //@formatter:off
				.email(entity.getEmail())
				.authority(entity.getAuthority())
				.dateOfBirth(entity.getDateOfBirth())
				.spendingMoney(entity.getSpendingMoney())
				.links(createLinks(entity)).build(); //@formatter:on
	}

	public VisitorResource toModelWithPhoto(Visitor entity) {
		VisitorResource visitorResource = VisitorResource
				.builder() //@formatter:off
				.email(entity.getEmail())
				.authority(entity.getAuthority())
				.dateOfBirth(entity.getDateOfBirth())
				.spendingMoney(entity.getSpendingMoney())
				.links(createLinks(entity)).build(); //@formatter:on
		Optional.ofNullable(entity.getPhoto()).map(Photo::getPhoto).ifPresent(visitorResource::setPhoto);
		return visitorResource;
	}

	@Override
	public Visitor toEntity(VisitorResource resource) {
		return Visitor
				.builder() //@formatter:off
				.email(resource.getEmail())
				.password(passwordEncoder.encode(resource.getPassword()))
				.dateOfBirth(resource.getDateOfBirth())
				.spendingMoney(resource.getSpendingMoney())
				.photo(new Photo(resource.getPhoto())).build(); //@formatter:on
	}

	private Link[] createLinks(Visitor visitor) {
		List<Link> links = new ArrayList<>();
		links.add(createMeLinkWithSelfRel());
		links.add(LinkFactory.createUploadMoneyLink());
		AmusementPark amusementPark = visitor.getAmusementPark();
		if (amusementPark == null) {
			links.add(createAmusementParkLink());
		} else {
			Long amusementParkId = amusementPark.getId();
			Machine machine = visitor.getMachine();
			if (machine == null) {
				links.add(createMachineLink(amusementParkId));
				links.add(createVisitorLeavePark(amusementParkId));
				links.add(createGetOnMachineLink(amusementParkId, null));
				links.add(createAddGuestBookRegistryLink(amusementParkId));
			} else {
				links.add(createGetOffMachineLink(amusementParkId, machine.getId()));
			}
		}
		return links.toArray(new Link[links.size()]);
	}

}
