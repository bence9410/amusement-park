package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.AmusementParkIdUserEmail;
import hu.bence.amusementpark.entity.AmusementParkKnowUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AmusementParkKnowUserRepository
        extends JpaRepository<AmusementParkKnowUser, AmusementParkIdUserEmail> {

    @Query("Select count(*) from AmusementParkKnowUser ku where ku.amusementPark.id = :amusementParkId and ku.user.email = :userEmail")
    Long countByAmusementParkIdAndUserEmail(Long amusementParkId, String userEmail);

}
