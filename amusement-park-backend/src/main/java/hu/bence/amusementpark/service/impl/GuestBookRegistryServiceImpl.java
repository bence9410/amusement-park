package hu.bence.amusementpark.service.impl;

import hu.bence.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.GuestBookRegistry;
import hu.bence.amusementpark.entity.Visitor;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.GuestBookRegistryRepository;
import hu.bence.amusementpark.repository.VisitorRepository;
import hu.bence.amusementpark.service.GuestBookRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.bence.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.bence.amusementpark.exception.ExceptionUtil.ifNull;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestBookRegistryServiceImpl implements GuestBookRegistryService {

    private final AmusementParkRepository amusementParkRepository;
    private final VisitorRepository visitorRepository;
    private final GuestBookRegistryRepository guestBookRegistryRepository;

    @Override
    public void addRegistry(Long amusementParkId, String visitorEmail, String textOfRegistry) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        Visitor visitor = ifNull(visitorRepository.findById(visitorEmail), NO_VISITOR_IN_PARK_WITH_ID);
        guestBookRegistryRepository.save(GuestBookRegistry.builder().textOfRegistry(textOfRegistry)
                .visitor(visitor).amusementPark(amusementPark).build());
    }

    @Override
    public Page<GuestBookRegistrySearchResponseDto> findAll(GuestBookRegistrySearchRequestDto dto, Pageable pageable) {
        return guestBookRegistryRepository.findAll(dto, pageable);
    }

}
