package hu.beni.amusementpark.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
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

    @Tolerate
    protected GuestBookRegistry() {
        super();
    }

}
