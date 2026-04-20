package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.AmusementParkIdUserName;
import hu.bence.amusementpark.entity.AmusementParkKnowUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AmusementParkKnowUserRepository
        extends JpaRepository<AmusementParkKnowUser, AmusementParkIdUserName> {

    @Query("Select count(*) from AmusementParkKnowUser ku where ku.amusementPark.id = :amusementParkId and ku.user.name = :userName")
    Long countByAmusementParkIdAndUserName(Long amusementParkId, String userName);

}
