package hu.bence.amusementpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bence.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.service.GuestBookRegistryService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class GuestBookRegistryController {

    private final ObjectMapper objectMapper;
    private final GuestBookRegistryService guestBookRegistryService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(GuestBookRegistrySearchRequestDto.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8.toString()),
                            GuestBookRegistrySearchRequestDto.class));
                } catch (IOException e) {
                    throw new AmusementParkException("Wrong input!", e);
                }
            }
        });
    }

    @PostMapping("/amusement-parks/{amusementParkId}/guest-book-registries")
    public void addRegistry(@PathVariable Long amusementParkId,
                            @Size(min = 2, max = 100) @RequestBody String textOfRegistry,
                            Principal principal) {
        guestBookRegistryService.addRegistry(amusementParkId, principal.getName(), textOfRegistry);
    }

    @GetMapping("/amusement-parks/{amusementParkId}/guest-book-registries")
    public Page<GuestBookRegistrySearchResponseDto> findAllPaged(@PathVariable Long amusementParkId,
                                                                 @RequestParam(required = false) GuestBookRegistrySearchRequestDto input,
                                                                 @PageableDefault Pageable pageable) {
        if (input == null) {
            input = new GuestBookRegistrySearchRequestDto();
        }
        input.setAmusementParkId(amusementParkId);
        return guestBookRegistryService.findAll(input, pageable);
    }
}
