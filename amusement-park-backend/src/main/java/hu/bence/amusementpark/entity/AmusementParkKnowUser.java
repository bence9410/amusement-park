package hu.bence.amusementpark.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
public class AmusementParkKnowUser {

    @EmbeddedId
    private AmusementParkIdUserEmail id = new AmusementParkIdUserEmail();

    @CreationTimestamp
    private LocalDateTime dateOfFirstEnter;

    @MapsId("amusementParkId")
    @ManyToOne(fetch = LAZY)
    private AmusementPark amusementPark;

    @MapsId("userEmail")
    @ManyToOne(fetch = LAZY)
    private Users user;

    public AmusementParkKnowUser(AmusementPark amusementPark, Users user) {
        this.amusementPark = amusementPark;
        this.user = user;
    }

}
