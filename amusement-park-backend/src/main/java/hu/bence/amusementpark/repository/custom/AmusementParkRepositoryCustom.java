package hu.bence.amusementpark.repository.custom;

import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmusementParkRepositoryCustom {

    Page<AmusementParkSearchResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable);

}
