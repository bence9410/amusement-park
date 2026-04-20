package hu.beni.amusementpark.service;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmusementParkService {

    void save(AmusementPark amusementPark, String visitorEmail);

    Page<AmusementParkSearchResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
