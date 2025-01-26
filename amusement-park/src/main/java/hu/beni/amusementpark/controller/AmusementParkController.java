package hu.beni.amusementpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.resource.AmusementParkResource;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.factory.LinkFactory;
import hu.beni.amusementpark.mapper.AmusementParkMapper;
import hu.beni.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/amusement-parks")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class AmusementParkController {

    private final ObjectMapper objectMapper;
    private final AmusementParkService amusementParkService;
    private final AmusementParkMapper amusementParkMapper;
    private final PagedResourcesAssembler<AmusementParkDetailResponseDto> pagedResourcesAssembler;

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
    public AmusementParkResource save(@Valid @RequestBody AmusementParkResource amusementParkResource) {
        return amusementParkMapper
                .toModel(amusementParkService.save(amusementParkMapper.toEntity(amusementParkResource)));
    }

    @GetMapping
    public PagedModel<EntityModel<AmusementParkDetailResponseDto>> findAllPaged(
            @RequestParam(required = false) AmusementParkSearchRequestDto input, @PageableDefault Pageable pageable) {
        if (input == null) {
            input = new AmusementParkSearchRequestDto();
        }
        PagedModel<EntityModel<AmusementParkDetailResponseDto>> result = pagedResourcesAssembler
                .toModel(amusementParkService.findAll(input, pageable));

        result.getContent()
                .forEach(r -> r.add(new Link[]{LinkFactory.createAmusementParkSelfLink(r.getContent().getId()),
                        LinkFactory.createMachineLink(r.getContent().getId()), LinkFactory.createVisitorSignUpLink(),
                        LinkFactory.createVisitorEnterParkLink(r.getContent().getId())}));

        return result;
    }

    @GetMapping("/{amusementParkId}")
    public EntityModel<AmusementParkDetailResponseDto> findOne(@PathVariable Long amusementParkId) {
        AmusementParkDetailResponseDto amusementParkPageResponseDto = amusementParkService
                .findDetailById(amusementParkId);
        return EntityModel.of(amusementParkPageResponseDto, LinkFactory.createAmusementParkSelfLink(amusementParkId),
                LinkFactory.createVisitorEnterParkLink(amusementParkId),
                LinkFactory.createAddGuestBookRegistryLink(amusementParkId),
                LinkFactory.createMachineLink(amusementParkId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{amusementParkId}")
    public void delete(@PathVariable Long amusementParkId) {
        amusementParkService.delete(amusementParkId);
    }
}
