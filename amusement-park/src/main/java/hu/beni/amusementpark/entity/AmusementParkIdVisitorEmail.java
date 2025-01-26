package hu.beni.amusementpark.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class AmusementParkIdVisitorEmail implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long amusementParkId;

    private String visitorEmail;

}
