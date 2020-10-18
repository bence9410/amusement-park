package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.beni.amusementpark.entity.VisitorEvent;

public interface VisitorEventRepository extends JpaRepository<VisitorEvent, Long> {

}
