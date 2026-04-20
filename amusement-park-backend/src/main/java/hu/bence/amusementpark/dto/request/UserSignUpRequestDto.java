package hu.bence.amusementpark.dto.request;

import hu.bence.amusementpark.constants.Constants;
import hu.bence.amusementpark.validation.constraint.PasswordConfirmPasswordSameConstraint;
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
public class UserSignUpRequestDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

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
