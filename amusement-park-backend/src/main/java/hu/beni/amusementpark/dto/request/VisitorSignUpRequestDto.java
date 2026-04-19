package hu.beni.amusementpark.dto.request;

import hu.beni.amusementpark.constants.Constants;
import hu.beni.amusementpark.validation.constraint.PasswordConfirmPasswordSameConstraint;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordConfirmPasswordSameConstraint
public class VisitorSignUpRequestDto {

    @NotNull
    @Email(regexp = Constants.EMAIL_REGEXP)
    private String email;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_REGEXP)
    private String password;

    @NotNull
    @Pattern(regexp = Constants.PASSWORD_REGEXP)
    private String confirmPassword;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotNull
    @Size(min = 5, max = 1400000)
    private String photo;

}
