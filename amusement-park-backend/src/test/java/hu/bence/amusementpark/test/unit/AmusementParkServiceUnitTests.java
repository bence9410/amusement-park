package hu.bence.amusementpark.test.unit;

import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.AmusementParkService;
import hu.bence.amusementpark.service.impl.AmusementParkServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AmusementParkServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private UserRepository userRepository;

    private AmusementParkService amusementParkService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        userRepository = mock(UserRepository.class);
        amusementParkService = new AmusementParkServiceImpl(amusementParkRepository, userRepository);
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, userRepository);
    }

    @Test
    public void saveNegativeNoUser() {
        AmusementPark amusementPark = AmusementPark.builder().build();

        assertThatThrownBy(() -> amusementParkService.save(amusementPark, NAME))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, NAME));

        verify(userRepository).findById(NAME);
    }

    @Test
    public void savePositive() {
        AmusementPark amusementPark = AmusementPark.builder().build();
        Users user = Users.builder().name(NAME).build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));
        when(amusementParkRepository.save(amusementPark)).thenReturn(amusementPark);

        amusementParkService.save(amusementPark, NAME);

        assertNotNull(amusementPark.getOwner());
        verify(userRepository).findById(NAME);
        verify(amusementParkRepository).save(amusementPark);
    }

    @Test
    public void findAllPositive() {
        Page<AmusementParkSearchResponseDto> page = new PageImpl<>(
                Collections.singletonList(AmusementParkSearchResponseDto.builder().name("Bence's park").build()));
        Pageable pageable = PageRequest.of(0, 10);
        AmusementParkSearchRequestDto dto = new AmusementParkSearchRequestDto();
        when(amusementParkRepository.findAll(dto, pageable)).thenReturn(page);

        assertEquals(page, amusementParkService.findAll(dto, pageable));

        verify(amusementParkRepository).findAll(dto, pageable);
    }
}