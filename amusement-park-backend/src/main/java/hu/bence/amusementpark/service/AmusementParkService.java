package hu.bence.amusementpark.service;

import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmusementParkService {

    void save(AmusementPark amusementPark, String userEmail);

    Page<AmusementParkSearchResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
