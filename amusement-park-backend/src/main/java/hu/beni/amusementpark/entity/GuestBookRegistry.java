package hu.beni.amusementpark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
public class GuestBookRegistry {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String textOfRegistry;

    @CreationTimestamp
    private LocalDateTime dateOfRegistry;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private Visitor visitor;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private AmusementPark amusementPark;

}
