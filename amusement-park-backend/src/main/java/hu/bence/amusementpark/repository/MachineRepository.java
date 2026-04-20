package hu.bence.amusementpark.repository;

import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.repository.custom.MachineRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long>, MachineRepositoryCustom {

    @Query("Select m from Machine m where m.amusementPark.id = :amusementParkId and m.id = :machineId")
    Optional<Machine> findByAmusementParkIdAndMachineId(Long amusementParkId, Long machineId);

}
