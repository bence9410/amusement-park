package hu.beni.amusementpark.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
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

    @OneToMany(mappedBy = "amusementPark", cascade = REMOVE)
    private Set<VisitorEvent> visitorEvents;

    @Tolerate
    protected AmusementPark() {
        super();
    }

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
