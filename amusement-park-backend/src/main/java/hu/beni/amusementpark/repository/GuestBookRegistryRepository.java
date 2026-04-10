package hu.beni.amusementpark.repository;

import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.repository.custom.GuestBookRegistryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestBookRegistryRepository
        extends JpaRepository<GuestBookRegistry, Long>, GuestBookRegistryRepositoryCustom {

}
