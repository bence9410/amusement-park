package hu.bence.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmusementParkSearchResponseDto {

    private Long id;

    private String name;

    private Integer entranceFee;

    private String ownerName;

    private Long numberOfMachines;

    private Long numberOfGuestBookRegistries;

    private Long numberOfActiveUsers;

    private Long numberOfKnownUsers;

}
