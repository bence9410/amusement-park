package hu.beni.amusementpark.entity;

import hu.beni.amusementpark.enums.VisitorEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

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
