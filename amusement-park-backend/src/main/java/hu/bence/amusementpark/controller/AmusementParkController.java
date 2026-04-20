package hu.bence.amusementpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bence.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.mapper.AmusementParkMapper;
import hu.bence.amusementpark.service.AmusementParkService;
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
@RequestMapping("/api/amusement-parks")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class AmusementParkController {

    private final ObjectMapper objectMapper;
    private final AmusementParkService amusementParkService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AmusementParkSearchRequestDto.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8.toString()),
                            AmusementParkSearchRequestDto.class));
                } catch (IOException e) {
                    throw new AmusementParkException("Wrong input!", e);
                }
            }
        });
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void save(@Valid @RequestBody AmusementParkCreateRequestDto amusementParkCreateRequestDto, Principal principal) {
        amusementParkService.save(AmusementParkMapper.toEntity(amusementParkCreateRequestDto), principal.getName());
    }

    @GetMapping
    public Page<AmusementParkSearchResponseDto> findAllPaged(
            @RequestParam(required = false) AmusementParkSearchRequestDto input, @PageableDefault Pageable pageable) {
        if (input == null) {
            input = new AmusementParkSearchRequestDto();
        }
        return amusementParkService.findAll(input, pageable);
    }
}
