package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.impl.MachineServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MachineServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;

    private MachineService machineService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);

        machineService = new MachineServiceImpl(amusementParkRepository, machineRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository);
    }

    @Test
    public void addMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().build();

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
    }

    @Test
    public void addMachineNegativeTooExpensiveMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).capital(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId))
                .thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine))
                .isInstanceOf(AmusementParkException.class).hasMessage(MACHINE_IS_TOO_EXPENSIVE);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
    }

    @Test
    public void addMachineNegativeTooBigMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(Optional.of(80L));

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine))
                .isInstanceOf(AmusementParkException.class).hasMessage(MACHINE_IS_TOO_BIG);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
    }

    @Test
    public void addMachinePositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(80).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(Optional.of(20L));
        when(machineRepository.save(machine)).thenReturn(machine);

        assertEquals(machine, machineService.addMachine(amusementPark.getId(), machine));

        assertEquals(amusementPark, machine.getAmusementPark());

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).decreaseCapitalById(machine.getPrice(), amusementParkId);
        verify(machineRepository).save(machine);
    }

    @Test
    public void findByIdNegativeNoMachine() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        assertThatThrownBy(() -> machineService.findById(amusementParkId, machineId))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void findByIdPositive() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));

        assertEquals(machine, machineService.findById(amusementParkId, machineId));

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void findAllPositive() {
        Page<MachineSearchResponseDto> page = new PageImpl<>(
                Arrays.asList(MachineSearchResponseDto.builder().fantasyName("Titanic").build(),
                        MachineSearchResponseDto.builder().fantasyName("Super roller coaster").build()));
        Pageable pageable = PageRequest.of(0, 10);
        MachineSearchRequestDto dto = new MachineSearchRequestDto();

        when(machineRepository.findAll(dto, pageable)).thenReturn(page);

        assertEquals(page, machineService.findAll(dto, pageable));

        verify(machineRepository).findAll(dto, pageable);
    }

}
