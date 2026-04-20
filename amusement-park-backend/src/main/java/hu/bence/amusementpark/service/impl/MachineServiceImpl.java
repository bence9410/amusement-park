package hu.bence.amusementpark.service.impl;

import hu.bence.amusementpark.dto.request.MachineSearchRequestDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.MachineRepository;
import hu.bence.amusementpark.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.AMUSEMENT_PARK_NOT_OWNED_BY_YOU;
import static hu.bence.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.bence.amusementpark.exception.ExceptionUtil.ifNotEquals;
import static hu.bence.amusementpark.exception.ExceptionUtil.ifNull;

@Service
@Transactional
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;

    @Override
    public void addMachine(Long amusementParkId, Machine machine, String userName) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        ifNotEquals(userName, amusementPark.getOwner().getName(), AMUSEMENT_PARK_NOT_OWNED_BY_YOU);
        machine.setAmusementPark(amusementPark);
        machineRepository.save(machine);
    }

    @Override
    public Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable) {
        return machineRepository.findAll(dto, pageable);
    }

}
