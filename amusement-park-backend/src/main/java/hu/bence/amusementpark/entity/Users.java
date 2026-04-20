package hu.bence.amusementpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "email")
public class Users {

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
    @Range(min = 0)
    private Integer money;

    @NotNull
    @Range(min = 0)
    private Integer coupon;

    @OneToOne(fetch = LAZY, cascade = ALL)
    private Photo photo;

    @ManyToOne(fetch = LAZY)
    private AmusementPark amusementPark;

    @ManyToOne(fetch = LAZY)
    private Machine machine;

    @OneToMany(mappedBy = "owner")
    private Set<AmusementPark> ownedAmusementParks;

    @OneToMany(mappedBy = "user")
    private Set<GuestBookRegistry> guestBookRegistries;

    @OneToMany(mappedBy = "user")
    private Set<AmusementParkKnowUser> knownAmusementParks;

}
