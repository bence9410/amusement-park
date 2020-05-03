package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.Machine;

public interface MachineService {

	Machine addMachine(Long amusementParkId, Machine machine);

	Machine findOne(Long amusementParkId, Long machineId);

	Page<MachineSearchResponseDto> findAllPaged(MachineSearchRequestDto dto, Pageable pageable);

}
