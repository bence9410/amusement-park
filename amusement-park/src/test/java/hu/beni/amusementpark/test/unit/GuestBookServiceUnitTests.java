package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.service.impl.GuestBookRegistryServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GuestBookServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private VisitorRepository visitorRepository;
    private GuestBookRegistryRepository guestBookRegistryRepository;

    private GuestBookRegistryService guestBookService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        guestBookRegistryRepository = mock(GuestBookRegistryRepository.class);
        guestBookService = new GuestBookRegistryServiceImpl(amusementParkRepository, visitorRepository,
                guestBookRegistryRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, visitorRepository, guestBookRegistryRepository);
    }

    @Test
    public void findByIdNegativeNoGuestBook() {
        Long guestBookRegistryId = 0L;

        assertThatThrownBy(() -> guestBookService.findById(guestBookRegistryId))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_GUEST_BOOK_REGISTRY_WITH_ID);

        verify(guestBookRegistryRepository).findById(guestBookRegistryId);
    }

    @Test
    public void findByIdPositive() {
        GuestBookRegistry guestBookRegistry = GuestBookRegistry.builder().id(0L).build();
        Long guestBookRegistryId = guestBookRegistry.getId();

        when(guestBookRegistryRepository.findById(guestBookRegistryId)).thenReturn(Optional.of(guestBookRegistry));

        assertEquals(guestBookRegistry, guestBookService.findById(guestBookRegistryId));

        verify(guestBookRegistryRepository).findById(guestBookRegistryId);
    }

    @Test
    public void addRegistryNegativeNoAmusementPark() {
        Long amusementParkId = 0L;
        String visitorEmail = "benike@gmail.com";
        String textOfRegistry = OPINION_ON_THE_PARK;

        assertThatThrownBy(() -> guestBookService.addRegistry(amusementParkId, visitorEmail, textOfRegistry))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
    }

    @Test
    public void addRegistryNegativeNoVisitorInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        String visitorEmail = "benike@gmail.com";
        String textOfRegistry = OPINION_ON_THE_PARK;

        when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> guestBookService.addRegistry(amusementParkId, visitorEmail, textOfRegistry))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
    }

    @Test
    public void addRegistryPositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email("benike@gmail.com").build();
        String visitorEmail = visitor.getEmail();
        String textOfRegistry = OPINION_ON_THE_PARK;

        when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));
        GuestBookRegistry guestBookRegistry = GuestBookRegistry.builder().amusementPark(amusementPark)
                .textOfRegistry(textOfRegistry).visitor(visitor).build();
        when(guestBookRegistryRepository.save(any(GuestBookRegistry.class))).thenReturn(guestBookRegistry);

        assertEquals(guestBookRegistry, guestBookService.addRegistry(amusementParkId, visitorEmail, textOfRegistry));

        verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
        verify(guestBookRegistryRepository).save(any(GuestBookRegistry.class));
    }

}