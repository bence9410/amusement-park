package hu.bence.amusementpark.test.integration.service;

import hu.bence.amusementpark.dto.request.MachineSearchRequestDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.helper.ValidEntityFactory;
import hu.bence.amusementpark.service.MachineService;
import hu.bence.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MachineServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private MachineService machineService;

    @Test
    public void addMachineTest() {
        Machine machine = ValidEntityFactory.createMachine();

        machineService.addMachine(amusementParkId, machine, NAME);

        assertNotNull(machine.getId());
        assertNotNull(machine.getAmusementPark());
        assertEquals(amusementParkId, machine.getAmusementPark().getId());
        select++;
        insert++;
        assertStatements();
    }

    @Test
    public void findAllTest() {
        MachineSearchRequestDto dto = new MachineSearchRequestDto();
        dto.setAmusementParkId(amusementParkId);
        dto.setFantasyName("tani");

        Page<MachineSearchResponseDto> page = machineService.findAll(dto, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        select += 2;
        assertStatements();
    }

}
