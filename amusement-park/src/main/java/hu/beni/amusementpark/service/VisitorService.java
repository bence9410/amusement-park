package hu.beni.amusementpark.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorService extends UserDetailsService {

	Visitor findByEmail(String visitorEmail);

	Visitor signUp(Visitor visitor);

	void uploadMoney(Integer amount, String visitorEmail);

	Visitor leavePark(Long amusementParkId, String visitorEmail);

	Visitor enterPark(Long amusementParkId, String visitorEmail);

	Visitor getOnMachine(Long amusementParkId, Long machineId, String visitorEmail);

	Visitor getOffMachine(Long amusementParkId, Long machineId, String visitorEmail);

	List<Visitor> findAllVisitor();

	void delete(String visitorEmail);

	Visitor getOffMachineAndLeavePark(String visitorEmail);

}
