package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.impl.MachineServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.AMUSEMENT_PARK_NOT_OWNED_BY_YOU;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.StringParamConstants.EMAIL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MachineServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;

    private MachineService machineService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);

        machineService = new MachineServiceImpl(amusementParkRepository, machineRepository);
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository);
    }

    @Test
    public void addMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().build();

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void addMachineNegativeNotOwned() {
        Visitor visitor = Visitor.builder().email(EMAIL).build();
        AmusementPark amusementPark = AmusementPark.builder().id(10L).owner(visitor).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine, "wrong"))
                .isInstanceOf(AmusementParkException.class).hasMessage(AMUSEMENT_PARK_NOT_OWNED_BY_YOU);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void addMachinePositive() {
        Visitor visitor = Visitor.builder().email(EMAIL).build();
        AmusementPark amusementPark = AmusementPark.builder().id(10L).owner(visitor).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.save(machine)).thenReturn(machine);

        machineService.addMachine(amusementPark.getId(), machine, EMAIL);

        assertEquals(amusementPark, machine.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).save(machine);
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
