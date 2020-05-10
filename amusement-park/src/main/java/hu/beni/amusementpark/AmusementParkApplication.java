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
	@Profile("default")
	public ApplicationRunner applicationRunner(AmusementParkService amusementParkService, MachineService machineService,
			VisitorService visitorService, GuestBookRegistryService guestBookRegistryService,
			VisitorRepository visitorRepository) {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return args -> {

			Visitor visitor = Visitor
					.builder() // @formatter:off
					.email("bence@gmail.com")
					.password(encoder.encode("password"))
					.authority("ROLE_ADMIN")
					.spendingMoney(250)
					.dateOfBirth(LocalDate.of(1995, 05, 10))
					.photo("data:image/jpeg;base64,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").build(); // @formatter:on

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
	@Profile({ "oracleDB", "postgres" })
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