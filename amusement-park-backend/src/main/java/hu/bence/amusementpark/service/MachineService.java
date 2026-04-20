package hu.bence.amusementpark.service;

import hu.bence.amusementpark.dto.request.MachineSearchRequestDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import hu.bence.amusementpark.entity.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MachineService {

    void addMachine(Long amusementParkId, Machine machine, String userName);

    Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable);

}
