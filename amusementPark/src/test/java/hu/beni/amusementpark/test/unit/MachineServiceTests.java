package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.impl.MachineServiceImpl;

import static hu.beni.amusementpark.test.MyAssert.assertThrows;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MachineServiceTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;

    private MachineService machineService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        machineService = new MachineServiceImpl(amusementParkRepository, machineRepository, visitorRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }

    @Test
    public void addMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().build();

        assertThrows(() -> machineService.addMachine(amusementParkId, machine), AmusementParkException.class, NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
    }

    @Test
    public void addMachineNegativeTooExpensiveMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).capital(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId)).thenReturn(amusementPark);

        assertThrows(() -> machineService.addMachine(amusementParkId, machine), AmusementParkException.class, MACHINE_IS_TOO_EXPENSIVE);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
    }

    @Test
    public void addMachineNegativeTooBigMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId)).thenReturn(amusementPark);
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(80L);

        assertThrows(() -> machineService.addMachine(amusementParkId, machine), AmusementParkException.class, MACHINE_IS_TOO_BIG);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
    }

    @Test
    public void addMachinePositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId)).thenReturn(amusementPark);
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(20L);
        when(machineRepository.save(machine)).thenReturn(machine);

        assertEquals(machine, machineService.addMachine(amusementPark.getId(), machine));

        assertEquals(amusementPark, machine.getAmusementPark());

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).decreaseCapitalById(machine.getPrice(), amusementParkId);
        verify(machineRepository).save(machine);
    }

    @Test
    public void removeMachineNegativeNoMachine() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        assertThrows(() -> machineService.removeMachine(amusementParkId, machineId), AmusementParkException.class, NO_MACHINE_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void removeMachineNegativeVisitorsOnMachine() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).price(150).build();
        Long machineId = machine.getId();
        Long numberOfVisitorsOnMachine = 10L;

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.countByMachineId(machineId)).thenReturn(numberOfVisitorsOnMachine);

        assertThrows(() -> machineService.removeMachine(amusementParkId, machineId), AmusementParkException.class, VISITORS_ON_MACHINE);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).countByMachineId(machineId);
    }

    @Test
    public void removeMachinePositive() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).price(150).build();
        Long machineId = machine.getId();
        Long numberOfVisitorsOnMachine = 0L;

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.countByMachineId(machineId)).thenReturn(numberOfVisitorsOnMachine);

        machineService.removeMachine(amusementParkId, machineId);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).countByMachineId(machineId);
        verify(amusementParkRepository).incrementCapitalById(machine.getPrice(), amusementParkId);
        verify(machineRepository).delete(machineId);
    }

    @Test
    public void findOnePositive() {
        Machine machine = Machine.builder().id(0L).build();
        Long machineId = machine.getId();

        when(machineRepository.findOne(machineId)).thenReturn(machine);

        assertEquals(machine, machineService.findOne(machineId));

        verify(machineRepository).findOne(machineId);
    }

}
