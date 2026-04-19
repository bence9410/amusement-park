package hu.beni.amusementpark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

import static jakarta.persistence.CascadeType.REMOVE;
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
    @Size(min = 5, max = 20)
    private String name;

    @NotNull
    @Range(min = 500, max = 50000)
    private Integer capital;

    @NotNull
    @Range(min = 50, max = 5000)
    private Integer totalArea;

    @NotNull
    @Range(min = 5, max = 200)
    private Integer entranceFee;

    @OneToMany(mappedBy = "amusementPark", cascade = REMOVE)
    private Set<GuestBookRegistry> guestBookRegistries;

    @OneToMany(mappedBy = "amusementPark", cascade = REMOVE)
    private Set<Machine> machines;

    @OneToMany(mappedBy = "amusementPark")
    private Set<Visitor> activeVisitors;

    @OneToMany(mappedBy = "amusementPark", cascade = REMOVE)
    private Set<AmusementParkKnowVisitor> knownVisitors;

    @Tolerate
    public AmusementPark(Long id) {
        this.id = id;
    }

    @Tolerate
    public AmusementPark(Long id, Integer entranceFee) {
        this.id = id;
        this.entranceFee = entranceFee;
    }

    @Tolerate
    public AmusementPark(Long id, Integer capital, Integer totalArea) {
        this.id = id;
        this.capital = capital;
        this.totalArea = totalArea;
    }
}
