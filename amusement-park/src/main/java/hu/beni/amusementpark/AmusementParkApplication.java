package hu.beni.amusementpark;

import java.time.LocalDate;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

	@Bean
	@Profile("!performanceTest")
	public ApplicationRunner applicationRunner(AmusementParkService amusementParkService, MachineService machineService,
			VisitorService visitorService, GuestBookRegistryService guestBookRegistryService,
			VisitorRepository visitorRepository) {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return args -> {

			if (visitorRepository.existsById("bence@gmail.com")) {
				return;
			}

			Visitor visitor = Visitor
					.builder() // @formatter:off
					.email("bence@gmail.com")
					.password(encoder.encode("password"))
					.authority("ROLE_ADMIN")
					.spendingMoney(250)
					.dateOfBirth(LocalDate.of(1995, 05, 10)).build(); // @formatter:on

			visitorRepository.save(visitor);

			AmusementPark amusementPark = AmusementPark
					.builder() //@formatter:off
                .name("Bence's park")
                .capital(30000)
                .totalArea(2000)
                .entranceFee(50)
                .build(); //@formatter:on

			amusementParkService.save(amusementPark);

			visitorService.enterPark(amusementPark.getId(), visitor.getEmail());
			guestBookRegistryService.addRegistry(amusementPark.getId(), visitor.getEmail(), "I really enjoy it.");
			guestBookRegistryService.addRegistry(amusementPark.getId(), visitor.getEmail(), "It was good.");
			visitorService.leavePark(amusementPark.getId(), visitor.getEmail());

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
		            .fantasyName("Retro carousel")
		            .size(100)
		            .price(250)
		            .numberOfSeats(10)
		            .minimumRequiredAge(12)
		            .ticketPrice(10)
		            .type(MachineType.CAROUSEL).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
                .fantasyName("Magical dodgem")
                .size(150)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(12)
                .ticketPrice(10)
                .type(MachineType.DODGEM).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
	                .fantasyName("Electronic gokart")
	                .size(150)
	                .price(250)
	                .numberOfSeats(10)
	                .minimumRequiredAge(12)
	                .ticketPrice(10)
	                .type(MachineType.GOKART).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
	                .fantasyName("Titanic")
	                .size(150)
	                .price(250)
	                .numberOfSeats(10)
	                .minimumRequiredAge(12)
	                .ticketPrice(10)
	                .type(MachineType.SHIP).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
	                .fantasyName("Super roller coaster")
	                .size(150)
	                .price(250)
	                .numberOfSeats(10)
	                .minimumRequiredAge(12)
	                .ticketPrice(10)
	                .type(MachineType.ROLLER_COASTER).build()); //@formatter:on
		};
	}

	@Bean
	@Profile("performanceTest")
	public ApplicationRunner applicationRunnerOracle(VisitorRepository visitorRepository) {
		PasswordEncoder encoder = new BCryptPasswordEncoder(); // @formatter:off
		return args -> IntStream.range(0, 5).forEach(i -> visitorRepository.save(Visitor.builder() 
						.email("admin" + i + "@gmail.com")
						.password(encoder.encode("password"))
						.authority("ROLE_ADMIN")
						.spendingMoney(5000)
						.dateOfBirth(LocalDate.of(1994, 10, 22)).build())); // @formatter:on
	}
}