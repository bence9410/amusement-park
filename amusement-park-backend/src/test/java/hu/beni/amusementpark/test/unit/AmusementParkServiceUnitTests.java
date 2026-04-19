package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.impl.AmusementParkServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AmusementParkServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;

    private AmusementParkService amusementParkService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        amusementParkService = new AmusementParkServiceImpl(amusementParkRepository);
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository);
    }

    @Test
    public void savePositive() {
        AmusementPark amusementPark = AmusementPark.builder().build();
        when(amusementParkRepository.save(amusementPark)).thenReturn(amusementPark);

        amusementParkService.save(amusementPark);

        verify(amusementParkRepository).save(amusementPark);
    }

    @Test
    public void findAllPositive() {
        Page<AmusementParkSearchResponseDto> page = new PageImpl<>(
                Collections.singletonList(AmusementParkSearchResponseDto.builder().name("Beni parkja").build()));
        Pageable pageable = PageRequest.of(0, 10);
        AmusementParkSearchRequestDto dto = new AmusementParkSearchRequestDto();
        when(amusementParkRepository.findAll(dto, pageable)).thenReturn(page);

        assertEquals(page, amusementParkService.findAll(dto, pageable));

        verify(amusementParkRepository).findAll(dto, pageable);
    }
}