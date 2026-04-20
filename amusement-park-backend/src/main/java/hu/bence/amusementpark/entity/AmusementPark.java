package hu.bence.amusementpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
public class AmusementPark {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 5, max = 50)
    private String name;

    @NotNull
    @Range(min = 5, max = 200)
    private Integer entranceFee;

    @ManyToOne
    private Visitor owner;

    @OneToMany(mappedBy = "amusementPark")
    private Set<GuestBookRegistry> guestBookRegistries;

    @OneToMany(mappedBy = "amusementPark")
    private Set<Machine> machines;

    @OneToMany(mappedBy = "amusementPark")
    private Set<Visitor> activeVisitors;

    @OneToMany(mappedBy = "amusementPark")
    private Set<AmusementParkKnowVisitor> knownVisitors;

}
