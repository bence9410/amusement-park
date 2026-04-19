package hu.beni.amusementpark.entity;

import hu.beni.amusementpark.enums.MachineType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

}
