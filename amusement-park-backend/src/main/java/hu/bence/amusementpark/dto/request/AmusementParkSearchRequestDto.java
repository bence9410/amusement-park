package hu.bence.amusementpark.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmusementParkSearchRequestDto {

    private String name;

    private Integer minCapital;

    private Integer maxCapital;

    private Integer minTotalArea;

    private Integer maxTotalArea;

    private Integer minEntranceFee;

    private Integer maxEntranceFee;

    private Integer minMachines;

    private Integer maxMachines;

    private Integer minGuestBookRegistries;

    private Integer maxGuestBookRegistries;

    private Integer minActiveUsers;

    private Integer maxActiveUsers;

    private Integer minKnownUsers;

    private Integer maxKnownUsers;

}
