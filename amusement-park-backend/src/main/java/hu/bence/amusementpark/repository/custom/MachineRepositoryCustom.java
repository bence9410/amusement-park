package hu.bence.amusementpark.repository.custom;

import hu.bence.amusementpark.dto.request.MachineSearchRequestDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MachineRepositoryCustom {

    Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable);

}
