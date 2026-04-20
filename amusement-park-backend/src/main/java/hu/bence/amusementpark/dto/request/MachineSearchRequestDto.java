package hu.bence.amusementpark.dto.request;

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

    private Integer minMinimumRequiredAge;

    private Integer maxMinimumRequiredAge;

    private Integer minTicketPrice;

    private Integer maxTicketPrice;

    private Integer minNumberOfUsersOnMachine;

    private Integer maxNumberOfUsersOnMachine;

}
