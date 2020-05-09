package hu.beni.amusementpark.dto.resource;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import hu.beni.amusementpark.constraint.PasswordConfirmPasswordSameConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@PasswordConfirmPasswordSameConstraint
public class VisitorResource extends RepresentationModel<VisitorResource> {

	@NotNull
	@Email(regexp = "^(.+)@(.+)$")
	private String email;

	@NotNull
	@Size(min = 5, max = 25)
	private String password;

	@NotNull
	@Size(min = 5, max = 25)
	private String confirmPassword;

	@Null
	private String authority;

	@NotNull
	@Past
	private LocalDate dateOfBirth;

	@Null
	private Integer spendingMoney;

	private String photo;

	public VisitorResource() {
		super();
	}

	@Builder
	public VisitorResource(String email, String password, String confirmPassword, String authority,
			LocalDate dateOfBirth, Integer spendingMoney, String photo, Link[] links) {
		super();
		this.email = email;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.authority = authority;
		this.dateOfBirth = dateOfBirth;
		this.spendingMoney = spendingMoney;
		this.photo = photo;
		Optional.ofNullable(links).ifPresent(this::add);
	}

}
