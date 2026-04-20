package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.GuestBookRegistry;
import hu.bence.amusementpark.repository.custom.GuestBookRegistryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestBookRegistryRepository
        extends JpaRepository<GuestBookRegistry, Long>, GuestBookRegistryRepositoryCustom {

}
