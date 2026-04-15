package hu.beni.amusementpark.dto.request;

import lombok.Data;

@Data
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

    private Integer minActiveVisitors;

    private Integer maxActiveVisitors;

    private Integer minKnownVisitors;

    private Integer maxKnownVisitors;

}
