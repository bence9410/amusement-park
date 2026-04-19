package hu.beni.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineSearchResponseDto {

    private Long id;

    private String fantasyName;

    private Integer minimumRequiredAge;

    private Integer ticketPrice;

    private String video;

    private Integer videoLengthInSeconds;

    private Long numberOfVisitorsOnMachine;
}
