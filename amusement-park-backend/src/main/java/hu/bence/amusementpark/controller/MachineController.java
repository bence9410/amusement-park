package hu.bence.amusementpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bence.amusementpark.dto.request.MachineCreateRequestDto;
import hu.bence.amusementpark.dto.request.MachineSearchRequestDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.mapper.MachineMapper;
import hu.bence.amusementpark.service.MachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@RestController
@RequestMapping("/api/amusement-parks/{amusementParkId}/machines")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class MachineController {

    private final ObjectMapper objectMapper;
    private final MachineService machineService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(MachineSearchRequestDto.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8),
                            MachineSearchRequestDto.class));
                } catch (IOException e) {
                    throw new AmusementParkException("Wrong input!", e);
                }
            }
        });
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public void addMachine(@PathVariable Long amusementParkId,
                           @Valid @RequestBody MachineCreateRequestDto machineCreateRequestDto, Principal principal) {
        machineService.addMachine(amusementParkId, MachineMapper.toEntity(machineCreateRequestDto), principal.getName());
    }

    @GetMapping
    public Page<MachineSearchResponseDto> findAllPaged(@PathVariable Long amusementParkId,
                                                       @RequestParam(required = false) MachineSearchRequestDto input,
                                                       @PageableDefault Pageable pageable) {
        if (input == null) {
            input = new MachineSearchRequestDto();
        }
        input.setAmusementParkId(amusementParkId);
        return machineService.findAll(input, pageable);
    }

}
