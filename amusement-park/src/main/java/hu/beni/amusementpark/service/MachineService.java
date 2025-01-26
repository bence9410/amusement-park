package hu.beni.amusementpark.service;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MachineService {

    Machine addMachine(Long amusementParkId, Machine machine);

    Machine findById(Long amusementParkId, Long machineId);

    Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable);

}
