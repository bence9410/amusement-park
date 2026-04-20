package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.AmusementParkIdVisitorEmail;
import hu.bence.amusementpark.entity.AmusementParkKnowVisitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AmusementParkKnowVisitorRepository
        extends JpaRepository<AmusementParkKnowVisitor, AmusementParkIdVisitorEmail> {

    @Query("Select count(*) from AmusementParkKnowVisitor kv where kv.amusementPark.id = :amusementParkId and kv.visitor.email = :visitorEmail")
    Long countByAmusementParkIdAndVisitorEmail(Long amusementParkId, String visitorEmail);

}
