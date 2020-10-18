package hu.beni.amusementpark.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import hu.beni.amusementpark.enums.VisitorEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorEvent {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private VisitorEventType type;

	@CreationTimestamp
	private LocalDateTime creationDateTime;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private AmusementPark amusementPark;

	@ManyToOne(fetch = FetchType.LAZY)
	private Machine machine;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Visitor visitor;

}
