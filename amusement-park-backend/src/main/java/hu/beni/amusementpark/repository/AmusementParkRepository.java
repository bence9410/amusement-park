package hu.beni.amusementpark.repository;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.custom.AmusementParkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AmusementParkRepository extends JpaRepository<AmusementPark, Long>, AmusementParkRepositoryCustom {

    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital - :amount where a.id = :amusementParkId")
    void decreaseCapitalById(Integer amount, Long amusementParkId);

    @Modifying
    @Query("Update AmusementPark a set a.capital = a.capital + :amount where a.id = :amusementParkId")
    void incrementCapitalById(Integer amount, Long amusementParkId);

    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id) from AmusementPark a where a.id = :amusementParkId")
    Optional<AmusementPark> findByIdReadOnlyId(Long amusementParkId);

    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.entranceFee) from AmusementPark a where a.id = :amusementParkId")
    Optional<AmusementPark> findByIdReadOnlyIdAndEntranceFee(Long amusementParkId);

    @Query("Select new hu.beni.amusementpark.entity.AmusementPark(a.id, a.capital, a.totalArea) from AmusementPark a where a.id = :amusementParkId")
    Optional<AmusementPark> findByIdReadOnlyIdAndCapitalAndTotalArea(Long amusementParkId);

}
