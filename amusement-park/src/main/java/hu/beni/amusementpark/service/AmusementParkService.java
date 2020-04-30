package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkPageResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;

public interface AmusementParkService {

	AmusementPark save(AmusementPark amusementPark);

	AmusementPark findById(Long amusementParkId);

	void delete(Long amusementParkId);

	Page<AmusementParkPageResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
