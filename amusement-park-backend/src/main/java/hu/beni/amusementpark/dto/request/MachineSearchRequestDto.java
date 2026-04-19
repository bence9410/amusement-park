package hu.beni.amusementpark.dto.request;

import hu.beni.amusementpark.enums.MachineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineSearchRequestDto {

    private Long amusementParkId;

    private String fantasyName;

    private Integer minSize;

    private Integer maxSize;

    private Integer minPrice;

    private Integer maxPrice;

    private Integer minNumberOfSeats;

    private Integer maxNumberOfSeats;

    private Integer minMinimumRequiredAge;

    private Integer maxMinimumRequiredAge;

    private Integer minTicketPrice;

    private Integer maxTicketPrice;

    private MachineType type;

}
