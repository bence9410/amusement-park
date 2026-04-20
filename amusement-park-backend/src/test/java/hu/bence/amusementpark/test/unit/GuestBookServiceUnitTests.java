package hu.bence.amusementpark.test.unit;

import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.GuestBookRegistry;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.GuestBookRegistryRepository;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.GuestBookRegistryService;
import hu.bence.amusementpark.service.impl.GuestBookRegistryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.bence.amusementpark.constants.ErrorMessageConstants.NO_USER_IN_PARK_WITH_ID;
import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static hu.bence.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class GuestBookServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private UserRepository userRepository;
    private GuestBookRegistryRepository guestBookRegistryRepository;

    private GuestBookRegistryService guestBookService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        userRepository = mock(UserRepository.class);
        guestBookRegistryRepository = mock(GuestBookRegistryRepository.class);
        guestBookService = new GuestBookRegistryServiceImpl(amusementParkRepository, userRepository,
                guestBookRegistryRepository);
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, userRepository, guestBookRegistryRepository);
    }

    @Test
    public void addRegistryNegativeNoAmusementPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> guestBookService.addRegistry(amusementParkId, NAME, OPINION_ON_THE_PARK))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void addRegistryNegativeNoUserInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> guestBookService.addRegistry(amusementParkId, NAME, OPINION_ON_THE_PARK))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
    }

    @Test
    public void addRegistryPositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).build();
        String textOfRegistry = OPINION_ON_THE_PARK;

        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        guestBookService.addRegistry(amusementParkId, NAME, textOfRegistry);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
        verify(guestBookRegistryRepository).save(GuestBookRegistry.builder()
                .amusementPark(amusementPark).textOfRegistry(textOfRegistry).user(user).build());
    }

}