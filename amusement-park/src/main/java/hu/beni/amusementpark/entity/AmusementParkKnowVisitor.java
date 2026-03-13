package hu.beni.amusementpark.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;


@Data
@Entity
@EqualsAndHashCode(of = "id")
public class AmusementParkKnowVisitor {

    @EmbeddedId
    private AmusementParkIdVisitorEmail id = new AmusementParkIdVisitorEmail();

    @CreationTimestamp
    private LocalDateTime dateOfFirstEnter;

    @MapsId("amusementParkId")
    @ManyToOne(fetch = LAZY)
    private AmusementPark amusementPark;

    @MapsId("visitorEmail")
    @ManyToOne(fetch = LAZY)
    private Visitor visitor;

    public AmusementParkKnowVisitor(AmusementPark amusementPark, Visitor visitor) {
        this.amusementPark = amusementPark;
        this.visitor = visitor;
    }

    protected AmusementParkKnowVisitor() {
        super();
    }

}
