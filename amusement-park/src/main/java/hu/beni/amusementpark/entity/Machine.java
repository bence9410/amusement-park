package hu.beni.amusementpark.entity;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import hu.beni.amusementpark.enums.MachineType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Data
@Entity
@Builder
@EqualsAndHashCode(of = "id")
public class Machine {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 25)
	private String fantasyName;

	@NotNull
	@Range(min = 20, max = 750)
	@Column(name = "Size_Of_Machine")
	private Integer size;

	@NotNull
	@Range(min = 50, max = 2000)
	private Integer price;

	@NotNull
	@Range(min = 5, max = 250)
	private Integer numberOfSeats;

	@NotNull
	@Range(max = 21)
	private Integer minimumRequiredAge;

	@NotNull
	@Range(min = 5, max = 30)
	private Integer ticketPrice;

	@NotNull
	@Enumerated(STRING)
	private MachineType type;

	@ManyToOne(fetch = LAZY, optional = false)
	private AmusementPark amusementPark;

	@OneToMany(mappedBy = "machine")
	private List<Visitor> visitors;

	@OneToMany(mappedBy = "machine")
	private List<VisitorEvent> visitorEvents;

	@Tolerate
	protected Machine() {
		super();
	}

}
