package hu.bence.amusementpark.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookRegistrySearchRequestDto {

    @JsonIgnore
    private Long amusementParkId;

    private String visitorEmail;

    private String textOfRegistry;

    private LocalDateTime minDateOfRegistry;

    private LocalDateTime maxDateOfRegistry;

}
