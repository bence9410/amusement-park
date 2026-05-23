package hu.bence.amusementpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

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
    @Size(min = 5, max = 50)
    @Column(unique = true)
    private String fantasyName;

    @NotNull
    @Range(max = 21)
    private Integer minimumRequiredAge;

    @NotNull
    @Range(min = 1, max = 30)
    private Integer ticketPrice;

    @NotNull
    @Size(min = 3)
    private String video;

    @NotNull
    @Range(min = 5, max = 300)
    private Integer videoLengthInSeconds;

    @ManyToOne(fetch = LAZY, optional = false)
    private AmusementPark amusementPark;

    @OneToMany(mappedBy = "machine")
    private Set<Users> users;

}
