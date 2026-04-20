package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    @Query("Select count(*) from Users u where u.email = :userEmail")
    Long countByEmail(String userEmail);

    @Modifying
    @Query("Update Users u set u.money = u.money + :amount where u.email = :userEmail")
    void incrementSpendingMoneyByEmail(Integer amount, String userEmail);

    @Query("Select u from Users u where u.amusementPark.id = :amusementParkId and u.email = :userEmail")
    Optional<Users> findByAmusementParkIdAndUserEmail(Long amusementParkId, String userEmail);

    @Query("Select u from Users u where u.amusementPark.id = :amusementParkId and u.machine.id = :machineId and u.email = :userEmail")
    Optional<Users> findByAmusementParkIdAndMachineIdAndUserEmail(Long amusementParkId, Long machineId, String userEmail);

}
