package hu.beni.amusementpark.repository;

import hu.beni.amusementpark.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, String> {

    @Query("Select count(*) from Visitor v where v.email = :visitorEmail")
    Long countByEmail(String visitorEmail);

    @Modifying
    @Query("Update Visitor v set v.money = v.money + :amount where v.email = :visitorEmail")
    void incrementSpendingMoneyByEmail(Integer amount, String visitorEmail);

    @Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.email = :visitorEmail")
    Optional<Visitor> findByAmusementParkIdAndVisitorEmail(Long amusementParkId, String visitorEmail);

    @Query("Select v from Visitor v where v.amusementPark.id = :amusementParkId and v.machine.id = :machineId and v.email = :visitorEmail")
    Optional<Visitor> findByAmusementParkIdAndMachineIdAndVisitorEmail(Long amusementParkId, Long machineId, String visitorEmail);

}
