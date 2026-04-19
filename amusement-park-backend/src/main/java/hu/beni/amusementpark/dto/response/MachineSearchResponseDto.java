package hu.beni.amusementpark.dto.response;

import hu.beni.amusementpark.enums.MachineType;
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

    private Integer size;

    private Integer price;

    private Integer numberOfSeats;

    private Integer minimumRequiredAge;

    private Integer ticketPrice;

    private MachineType type;
}
