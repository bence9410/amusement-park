package hu.beni.amusementpark.mapper;

import hu.beni.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.beni.amusementpark.entity.AmusementPark;

public class AmusementParkMapper {

    public static AmusementPark toEntity(AmusementParkCreateRequestDto dto) {
        return AmusementPark
                .builder()
                .name(dto.getName())
                .capital(dto.getCapital())
                .totalArea(dto.getTotalArea())
                .entranceFee(dto.getEntranceFee()).build();
    }
}