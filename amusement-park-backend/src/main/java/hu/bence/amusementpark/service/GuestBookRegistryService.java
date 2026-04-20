package hu.bence.amusementpark.service;

import hu.bence.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GuestBookRegistryService {

    void addRegistry(Long amusementParkId, String userEmail, String textOfRegistry);

    Page<GuestBookRegistrySearchResponseDto> findAll(GuestBookRegistrySearchRequestDto dto, Pageable pageable);

}
