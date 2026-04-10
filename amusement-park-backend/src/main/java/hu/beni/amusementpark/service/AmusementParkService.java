package hu.beni.amusementpark.service;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmusementParkService {

    AmusementPark save(AmusementPark amusementPark);

    AmusementPark findById(Long amusementParkId);

    AmusementParkDetailResponseDto findDetailById(Long amusementParkId);

    void delete(Long amusementParkId);

    Page<AmusementParkDetailResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
