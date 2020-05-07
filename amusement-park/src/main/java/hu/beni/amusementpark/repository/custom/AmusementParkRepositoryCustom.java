package hu.beni.amusementpark.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;

public interface AmusementParkRepositoryCustom {

	Page<AmusementParkDetailResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
