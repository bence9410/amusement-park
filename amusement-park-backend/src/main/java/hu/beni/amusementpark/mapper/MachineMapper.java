package hu.beni.amusementpark.mapper;

import hu.beni.amusementpark.dto.request.MachineCreateRequestDto;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.enums.MachineType;

public class MachineMapper {

    public static Machine toEntity(MachineCreateRequestDto dto) {
        return Machine.builder()
                .fantasyName(dto.getFantasyName())
                .size(dto.getSize())
                .price(dto.getPrice())
                .numberOfSeats(dto.getNumberOfSeats())
                .minimumRequiredAge(dto.getMinimumRequiredAge())
                .ticketPrice(dto.getTicketPrice())
                .type(MachineType.valueOf(dto.getType())).build();
    }
}
