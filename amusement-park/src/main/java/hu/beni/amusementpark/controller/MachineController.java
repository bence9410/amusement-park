package hu.beni.amusementpark.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.resource.MachineResource;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.factory.LinkFactory;
import hu.beni.amusementpark.mapper.MachineMapper;
import hu.beni.amusementpark.service.MachineService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/amusement-parks/{amusementParkId}/machines")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class MachineController {

	private final ObjectMapper objectMapper;
	private final MachineService machineService;
	private final MachineMapper machineMapper;
	private final PagedResourcesAssembler<MachineSearchResponseDto> pagedResourceAssembler;

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
	public MachineResource addMachine(@PathVariable Long amusementParkId,
			@Valid @RequestBody MachineResource machineResource) {
		return machineMapper
				.toModel(machineService.addMachine(amusementParkId, machineMapper.toEntity(machineResource)));
	}

	@GetMapping("/{machineId}")
	public MachineResource findById(@PathVariable Long amusementParkId, @PathVariable Long machineId) {
		return machineMapper.toModel(machineService.findById(amusementParkId, machineId));
	}

	@GetMapping
	public PagedModel<EntityModel<MachineSearchResponseDto>> findAllPaged(@PathVariable Long amusementParkId,
			@RequestParam(required = false) MachineSearchRequestDto input, @PageableDefault Pageable pageable) {
		if (input == null) {
			input = new MachineSearchRequestDto();
		}
		input.setAmusementParkId(amusementParkId);
		PagedModel<EntityModel<MachineSearchResponseDto>> result = pagedResourceAssembler
				.toModel(machineService.findAll(input, pageable));
		result.getContent()
				.forEach(r -> r.add(LinkFactory.createGetOnMachineLink(amusementParkId, r.getContent().getId())));
		return result;
	}

}
