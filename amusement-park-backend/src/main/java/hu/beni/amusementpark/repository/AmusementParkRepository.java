package hu.beni.amusementpark.repository;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.custom.AmusementParkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmusementParkRepository extends JpaRepository<AmusementPark, Long>, AmusementParkRepositoryCustom {

}
