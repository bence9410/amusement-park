package hu.bence.amusementpark.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class AmusementParkIdUserEmail implements Serializable {

    private Long amusementParkId;

    private String userEmail;

}
