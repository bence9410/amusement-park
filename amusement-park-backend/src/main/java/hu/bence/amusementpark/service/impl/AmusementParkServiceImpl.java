package hu.bence.amusementpark.service.impl;

import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.bence.amusementpark.constants.ErrorMessageConstants.NAME_ALREADY_TAKEN;
import static hu.bence.amusementpark.exception.ExceptionUtil.ifNotZero;
import static hu.bence.amusementpark.exception.ExceptionUtil.ifNull;

@Service
@Transactional
@RequiredArgsConstructor
public class AmusementParkServiceImpl implements AmusementParkService {

    private final AmusementParkRepository amusementParkRepository;
    private final UserRepository userRepository;

    @Override
    public AmusementPark save(AmusementPark amusementPark, String userName) {
        Users user = ifNull(userRepository.findById(userName),
                String.format(COULD_NOT_FIND_USER, userName));
        ifNotZero(amusementParkRepository.countByName(amusementPark.getName()),
                String.format(NAME_ALREADY_TAKEN, amusementPark.getName()));
        amusementPark.setOwner(user);
        return amusementParkRepository.save(amusementPark);
    }

    @Override
    public Page<AmusementParkSearchResponseDto> findAll(AmusementParkSearchRequestDto dto, Pageable pageable) {
        return amusementParkRepository.findAll(dto, pageable);
    }
}
