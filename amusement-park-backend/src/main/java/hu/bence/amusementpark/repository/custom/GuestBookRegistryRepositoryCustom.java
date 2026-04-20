package hu.bence.amusementpark.repository.custom;

import hu.bence.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestBookRegistryRepositoryCustom {

    Page<GuestBookRegistrySearchResponseDto> findAll(GuestBookRegistrySearchRequestDto dto, Pageable pageable);

}
