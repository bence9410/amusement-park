package hu.beni.amusementpark.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuestBookRegistrySearchRequestDto {

    @JsonIgnore
    private Long amusementParkId;

    private String visitorEmail;

    private String textOfRegistry;

    private LocalDateTime dateOfRegistryMin;

    private LocalDateTime dateOfRegistryMax;

}
