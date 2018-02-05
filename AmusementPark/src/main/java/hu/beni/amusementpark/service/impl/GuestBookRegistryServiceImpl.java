package hu.beni.amusementpark.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static hu.beni.amusementpark.exception.ExceptionUtil.*;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestBookRegistryServiceImpl implements GuestBookRegistryService{

	private final AmusementParkRepository amusementParkRepository;
	private final VisitorRepository visitorRepository;
	private final GuestBookRegistryRepository guestBookRegistryRepository;
	
        @Override
	public GuestBookRegistry findOne(Long guestBookRegistryId) {
		return guestBookRegistryRepository.findById(guestBookRegistryId).orElseGet(() -> null);
	}
	
        @Override
	public GuestBookRegistry addRegistry(Long amusementParkId, Long visitorId, String textOfRegistry) {
		AmusementPark amusementPark = amusementParkRepository.findByIdReadOnlyId(amusementParkId);
		exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
		Visitor visitor = visitorRepository.findById(visitorId).orElseGet(() -> null);
		exceptionIfNull(visitor, NO_VISITOR_IN_PARK_WITH_ID);
		return guestBookRegistryRepository.save(GuestBookRegistry.builder().textOfRegistry(textOfRegistry)
				.visitor(visitor).amusementPark(amusementPark).build());
	}
	
}