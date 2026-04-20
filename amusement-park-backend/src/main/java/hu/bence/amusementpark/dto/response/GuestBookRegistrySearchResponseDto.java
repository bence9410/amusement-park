package hu.bence.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookRegistrySearchResponseDto {

    private String userName;

    private String textOfRegistry;

    private LocalDateTime dateOfRegistry;

}
