package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.repository.custom.AmusementParkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmusementParkRepository extends JpaRepository<AmusementPark, Long>, AmusementParkRepositoryCustom {

}
