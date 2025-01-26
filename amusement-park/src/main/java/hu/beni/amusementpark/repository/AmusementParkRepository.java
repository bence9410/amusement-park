package hu.beni.amusementpark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.custom.AmusementParkRepositoryCustom;

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

	@Query("Select new hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto(a.id, a.name, a.capital, a.totalArea, a.entranceFee, "
			+ "(Select count(m) from Machine m where m.amusementPark = a), (Select count(g) from GuestBookRegistry g where g.amusementPark = a), "
			+ "(Select count(v) from Visitor v where v.amusementPark = a), (Select count(akv) from AmusementParkKnowVisitor akv where akv.amusementPark = a)) "
			+ "from AmusementPark a where a.id = :amusementParkId")
	Optional<AmusementParkDetailResponseDto> findDetailById(Long amusementParkId);

}
