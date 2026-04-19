package hu.beni.amusementpark.mapper;

import hu.beni.amusementpark.dto.request.MachineCreateRequestDto;
import hu.beni.amusementpark.entity.Machine;

public class MachineMapper {

    public static Machine toEntity(MachineCreateRequestDto dto) {
        return Machine.builder()
                .fantasyName(dto.getFantasyName())
                .minimumRequiredAge(dto.getMinimumRequiredAge())
                .ticketPrice(dto.getTicketPrice())
                .video(dto.getVideo())
                .videoLengthInSeconds(dto.getVideoLengthInSeconds()).build();
    }
}
