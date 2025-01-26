package hu.beni.amusementpark.repository.custom;

import hu.beni.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.beni.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestBookRegistryRepositoryCustom {

    Page<GuestBookRegistrySearchResponseDto> findAll(GuestBookRegistrySearchRequestDto dto, Pageable pageable);

}
