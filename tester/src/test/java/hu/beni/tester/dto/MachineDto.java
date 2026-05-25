package hu.beni.tester.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineDto {

    private Long id;

    private String fantasyName;

    private Integer minimumRequiredAge;

    private Integer ticketPrice;

    private String video;

    private Integer videoLengthInSeconds;

}
