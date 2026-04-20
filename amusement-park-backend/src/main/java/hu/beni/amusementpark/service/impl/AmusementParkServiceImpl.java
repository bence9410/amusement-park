package hu.beni.amusementpark.service.impl;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;

@Service
@Transactional
@RequiredArgsConstructor
public class AmusementParkServiceImpl implements AmusementParkService {

    private final AmusementParkRepository amusementParkRepository;
    private final VisitorRepository visitorRepository;

    @Override
    public void save(AmusementPark amusementPark, String visitorEmail) {
        Visitor visitor = ifNull(visitorRepository.findById(visitorEmail),
                String.format(COULD_NOT_FIND_USER, visitorEmail));
        amusementPark.setOwner(visitor);
        amusementParkRepository.save(amusementPark);
    }

    @Override
    public Page<AmusementParkSearchResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable) {
        return amusementParkRepository.findAll(dto, pageable);
    }
}
