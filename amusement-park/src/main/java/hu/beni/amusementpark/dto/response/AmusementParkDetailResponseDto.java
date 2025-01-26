package hu.beni.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AmusementParkDetailResponseDto {

    private Long id;

    private String name;

    private Integer capital;

    private Integer totalArea;

    private Integer entranceFee;

    private Long numberOfMachines;

    private Long numberOfGuestBookRegistries;

    private Long numberOfActiveVisitors;

    private Long numberOfKnownVisitors;

}
