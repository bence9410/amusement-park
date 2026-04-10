package hu.beni.amusementpark.entity;

import hu.beni.amusementpark.enums.VisitorEventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

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
