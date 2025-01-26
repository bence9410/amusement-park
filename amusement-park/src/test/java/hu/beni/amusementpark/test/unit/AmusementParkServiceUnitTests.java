package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITORS_IN_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AmusementParkServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private VisitorRepository visitorRepository;

    private AmusementParkService amusementParkService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        amusementParkService = new AmusementParkServiceImpl(amusementParkRepository, visitorRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, visitorRepository);
    }

    @Test
    public void savePositive() {
        AmusementPark amusementPark = AmusementPark.builder().build();

        when(amusementParkRepository.save(amusementPark)).thenReturn(amusementPark);

        assertEquals(amusementPark, amusementParkService.save(amusementPark));

        verify(amusementParkRepository).save(amusementPark);
    }

    @Test
    public void findByIdNegativeNoPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> amusementParkService.findById(amusementParkId))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void findByIdPositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();

        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertEquals(amusementPark, amusementParkService.findById(amusementParkId));

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void deleteNegativeVisitorsInPark() {
        Long amusementParkId = 0L;
        Long numberOfVisitorsInPark = 10L;

        when(visitorRepository.countByAmusementParkId(amusementParkId)).thenReturn(numberOfVisitorsInPark);

        assertThatThrownBy(() -> amusementParkService.delete(amusementParkId))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITORS_IN_PARK);

        verify(visitorRepository).countByAmusementParkId(amusementParkId);
    }

    @Test
    public void deleteNegativeNoParkWithId() {
        Long amusementParkId = 0L;
        Long numberOfVisitorsInPark = 0L;

        when(visitorRepository.countByAmusementParkId(amusementParkId)).thenReturn(numberOfVisitorsInPark);

        assertThatThrownBy(() -> amusementParkService.delete(amusementParkId))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(visitorRepository).countByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void deletePositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Long numberOfVisitorsInPark = 0L;

        when(visitorRepository.countByAmusementParkId(amusementParkId)).thenReturn(numberOfVisitorsInPark);
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        amusementParkService.delete(amusementParkId);

        verify(visitorRepository).countByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).findById(amusementParkId);
        verify(amusementParkRepository).delete(amusementPark);
    }

    @Test
    public void findAllPositive() {
        Page<AmusementParkDetailResponseDto> page = new PageImpl<>(
                Arrays.asList(AmusementParkDetailResponseDto.builder().name("Beni parkja").build(),
                        AmusementParkDetailResponseDto.builder().name("Jeni parkja").build()));
        Pageable pageable = PageRequest.of(0, 10);
        AmusementParkSearchRequestDto dto = new AmusementParkSearchRequestDto();

        when(amusementParkRepository.findAll(dto, pageable)).thenReturn(page);

        assertEquals(page, amusementParkService.findAll(dto, pageable));

        verify(amusementParkRepository).findAll(dto, pageable);
    }
}