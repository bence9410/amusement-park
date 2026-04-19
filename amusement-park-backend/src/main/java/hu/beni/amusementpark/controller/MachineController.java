package hu.beni.amusementpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.beni.amusementpark.dto.request.MachineCreateRequestDto;
import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.mapper.MachineMapper;
import hu.beni.amusementpark.service.MachineService;
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
                    setValue(objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8.toString()),
                            MachineSearchRequestDto.class));
                } catch (IOException e) {
                    throw new AmusementParkException("Wrong input!", e);
                }
            }
        });
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void addMachine(@PathVariable Long amusementParkId,
                           @Valid @RequestBody MachineCreateRequestDto machineResource) {
        machineService.addMachine(amusementParkId, MachineMapper.toEntity(machineResource));
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
