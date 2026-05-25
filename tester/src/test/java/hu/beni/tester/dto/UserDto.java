package hu.beni.tester.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String name;

    private String password;

    private String confirmPassword;

    private LocalDate dateOfBirth;

    private String authority;

    private Integer money;

    private String photo;

}
