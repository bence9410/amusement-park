package hu.beni.tester.resource;

import java.time.LocalDate;

import org.springframework.hateoas.RepresentationModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VisitorResource extends RepresentationModel<VisitorResource> {

	private String email;

	private String password;

	private String confirmPassword;

	private LocalDate dateOfBirth;

	private Integer spendingMoney;

	private String photo;

}
