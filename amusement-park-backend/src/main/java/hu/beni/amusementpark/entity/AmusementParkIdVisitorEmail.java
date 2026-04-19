package hu.beni.amusementpark.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class AmusementParkIdVisitorEmail implements Serializable {

    private Long amusementParkId;

    private String visitorEmail;

}
