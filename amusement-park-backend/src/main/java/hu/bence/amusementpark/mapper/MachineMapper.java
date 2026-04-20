package hu.bence.amusementpark.mapper;

import hu.bence.amusementpark.dto.request.MachineCreateRequestDto;
import hu.bence.amusementpark.entity.Machine;

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
