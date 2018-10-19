package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hu.beni.amusementpark.entity.AmusementParkIdVisitorId;
import hu.beni.amusementpark.entity.AmusementParkKnowVisitor;

public interface AmusementParkKnowVisitorRepository
		extends JpaRepository<AmusementParkKnowVisitor, AmusementParkIdVisitorId> {

	@Query("Select count(*) from AmusementParkKnowVisitor kv where kv.amusementPark.id = :amusementParkId and kv.visitor.email = :visitorEmail")
	Long countByAmusementParkIdAndVisitorEmail(Long amusementParkId, String visitorEmail);

}
