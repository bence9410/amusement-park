package hu.beni.amusementpark.service.impl;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifFirstLessThanSecond;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;

@Service
@Transactional
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;

    @Override
    public Machine addMachine(Long amusementParkId, Machine machine) {
        AmusementPark amusementPark = ifNull(
                amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId),
                NO_AMUSEMENT_PARK_WITH_ID);
        checkForMoneyAndFreeArea(amusementPark, machine);
        return buyMachine(amusementPark, machine);
    }

    private void checkForMoneyAndFreeArea(AmusementPark amusementPark, Machine machine) {
        ifFirstLessThanSecond(amusementPark.getCapital(), machine.getPrice(), MACHINE_IS_TOO_EXPENSIVE);
        ifFirstLessThanSecond(amusementPark.getTotalArea(),
                machineRepository.sumAreaByAmusementParkId(amusementPark.getId()).orElse(0L) + machine.getSize(),
                MACHINE_IS_TOO_BIG);
    }

    private Machine buyMachine(AmusementPark amusementPark, Machine machine) {
        amusementParkRepository.decreaseCapitalById(machine.getPrice(), amusementPark.getId());
        machine.setAmusementPark(amusementPark);
        return machineRepository.save(machine);
    }

    @Override
    public Machine findById(Long amusementParkId, Long machineId) {
        return ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
                NO_MACHINE_IN_PARK_WITH_ID);
    }

    @Override
    public Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable) {
        return machineRepository.findAll(dto, pageable);
    }

}
