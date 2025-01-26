package hu.beni.amusementpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.beni.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.beni.amusementpark.dto.resource.GuestBookRegistryResource;
import hu.beni.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.mapper.GuestBookRegistryMapper;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
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
    private final GuestBookRegistryMapper guestBookRegistryMapper;
    private final PagedResourcesAssembler<GuestBookRegistrySearchResponseDto> pagedResourceAssembler;

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

    @PostMapping("/amusement-parks/{amusementParkId}/visitors/guest-book-registries")
    public GuestBookRegistryResource addRegistry(@PathVariable Long amusementParkId,
                                                 @Size(min = 2, max = 100) @RequestBody String textOfRegistry, Principal principal) {
        return guestBookRegistryMapper
                .toModel(guestBookRegistryService.addRegistry(amusementParkId, principal.getName(), textOfRegistry));
    }

    @GetMapping("guest-book-registries/{guestBookRegistryId}")
    public GuestBookRegistryResource findById(@PathVariable Long guestBookRegistryId) {
        return guestBookRegistryMapper.toModel(guestBookRegistryService.findById(guestBookRegistryId));
    }

    @GetMapping("/amusement-parks/{amusementParkId}/visitors/guest-book-registries")
    public PagedModel<EntityModel<GuestBookRegistrySearchResponseDto>> findAllPaged(@PathVariable Long amusementParkId,
                                                                                    @RequestParam(required = false) GuestBookRegistrySearchRequestDto input,
                                                                                    @PageableDefault Pageable pageable) {
        if (input == null) {
            input = new GuestBookRegistrySearchRequestDto();
        }
        input.setAmusementParkId(amusementParkId);
        return pagedResourceAssembler.toModel(guestBookRegistryService.findAll(input, pageable));
    }
}
