package hu.beni.amusementpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static java.lang.Integer.MAX_VALUE;

@Data
@Entity
@Builder
@DynamicUpdate
@EqualsAndHashCode(of = "email")
public class Visitor {

    @Id
    @NotNull
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    @Size(min = 60, max = 60)
    private String password;

    @NotNull
    @Size(min = 5, max = 25)
    private String authority;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @CreationTimestamp
    private LocalDateTime dateOfSignUp;

    @NotNull
    @Range(min = 0, max = MAX_VALUE)
    private Integer spendingMoney;

    @OneToOne(fetch = LAZY, cascade = ALL)
    private Photo photo;

    @ManyToOne(fetch = LAZY)
    private AmusementPark amusementPark;

    @ManyToOne(fetch = LAZY)
    private Machine machine;

    @OneToMany(mappedBy = "visitor")
    private Set<GuestBookRegistry> guestBookRegistries;

    @OneToMany(mappedBy = "visitor")
    private Set<AmusementParkKnowVisitor> knownAmusementParks;

    @OneToMany(mappedBy = "visitor")
    private Set<VisitorEvent> visitorEvents;

    @Tolerate
    protected Visitor() {
        super();
    }

}
