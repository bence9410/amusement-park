package hu.beni.amusementpark.dto.resource;

import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.validation.constraint.EnumConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MachineResource extends RepresentationModel<MachineResource> {

	@Null
	private Long identifier;

	@NotNull
	@Size(min = 5, max = 25)
	private String fantasyName;

	@NotNull
	@Range(min = 20, max = 200)
	private Integer size;

	@NotNull
	@Range(min = 50, max = 2000)
	private Integer price;

	@NotNull
	@Range(min = 5, max = 50)
	private Integer numberOfSeats;

	@NotNull
	@Range(max = 21)
	private Integer minimumRequiredAge;

	@NotNull
	@Range(min = 5, max = 30)
	private Integer ticketPrice;

	@EnumConstraint(enumClazz = MachineType.class, message = "must be one of [CAROUSEL, ROLLER_COASTER, GOKART, DODGEM, SHIP]")
	private String type;

	@Builder
	public MachineResource(Long identifier, String fantasyName, Integer size, Integer price, Integer numberOfSeats,
			Integer minimumRequiredAge, Integer ticketPrice, String type, Link[] links) {
		super();
		this.identifier = identifier;
		this.fantasyName = fantasyName;
		this.size = size;
		this.price = price;
		this.numberOfSeats = numberOfSeats;
		this.minimumRequiredAge = minimumRequiredAge;
		this.ticketPrice = ticketPrice;
		this.type = type;
		Optional.ofNullable(links).ifPresent(this::add);
	}

}
