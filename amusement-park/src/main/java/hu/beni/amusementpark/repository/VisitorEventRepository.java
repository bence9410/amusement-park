package hu.beni.amusementpark.repository;

import hu.beni.amusementpark.entity.VisitorEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorEventRepository extends JpaRepository<VisitorEvent, Long> {

}
