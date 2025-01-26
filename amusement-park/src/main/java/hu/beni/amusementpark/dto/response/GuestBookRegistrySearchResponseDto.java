package hu.beni.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GuestBookRegistrySearchResponseDto {

    private String visitorEmail;

    private String textOfRegistry;

    private LocalDateTime dateOfRegistry;

}
