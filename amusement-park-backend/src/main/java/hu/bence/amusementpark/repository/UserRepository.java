package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    @Query("Select count(*) from Users u where u.name = :userName")
    Long countByName(String userName);

    @Modifying
    @Query("Update Users u set u.money = u.money + :amount where u.name = :userName")
    void incrementSpendingMoneyByName(Integer amount, String userName);

    @Query("Select u from Users u where u.amusementPark.id = :amusementParkId and u.name = :userName")
    Optional<Users> findByAmusementParkIdAndUserName(Long amusementParkId, String userName);

    @Query("Select u from Users u where u.amusementPark.id = :amusementParkId and u.machine.id = :machineId and u.name = :userName")
    Optional<Users> findByAmusementParkIdAndMachineIdAndUserName(Long amusementParkId, Long machineId, String userName);

}
