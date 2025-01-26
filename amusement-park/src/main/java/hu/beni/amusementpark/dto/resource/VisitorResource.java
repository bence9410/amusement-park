package hu.beni.amusementpark.dto.resource;

import hu.beni.amusementpark.constants.Constants;
import hu.beni.amusementpark.validation.constraint.PasswordConfirmPasswordSameConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Optional;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@PasswordConfirmPasswordSameConstraint
public class VisitorResource extends RepresentationModel<VisitorResource> {

    @NotNull
    @Email(regexp = Constants.EMAIL_REGEXP)
    private String email;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_REGEXP)
    private String password;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_REGEXP)
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
