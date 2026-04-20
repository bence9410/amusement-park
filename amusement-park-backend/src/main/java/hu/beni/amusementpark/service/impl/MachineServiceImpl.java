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

import static hu.beni.amusementpark.constants.ErrorMessageConstants.AMUSEMENT_PARK_NOT_OWNED_BY_YOU;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotEquals;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;

@Service
@Transactional
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;

    @Override
    public void addMachine(Long amusementParkId, Machine machine, String visitorEmail) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        ifNotEquals(visitorEmail, amusementPark.getOwner().getEmail(), AMUSEMENT_PARK_NOT_OWNED_BY_YOU);
        machine.setAmusementPark(amusementPark);
        machineRepository.save(machine);
    }

    @Override
    public Page<MachineSearchResponseDto> findAll(MachineSearchRequestDto dto, Pageable pageable) {
        return machineRepository.findAll(dto, pageable);
    }

}
