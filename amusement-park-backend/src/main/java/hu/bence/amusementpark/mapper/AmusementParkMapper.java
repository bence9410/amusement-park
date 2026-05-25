package hu.bence.amusementpark.mapper;

import hu.bence.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;

public class AmusementParkMapper {

    public static AmusementPark toEntity(AmusementParkCreateRequestDto dto) {
        return AmusementPark
                .builder()
                .name(dto.getName())
                .entranceFee(dto.getEntranceFee()).build();
    }

    public static AmusementParkSearchResponseDto toDto(AmusementPark entity){
        return AmusementParkSearchResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .entranceFee(entity.getEntranceFee()).build();
    }
}