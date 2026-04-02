package hu.beni.tester.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorResource extends RepresentationModel<VisitorResource> {

    private String email;

    private String password;

    private String confirmPassword;

    private LocalDate dateOfBirth;

    private Integer spendingMoney;

    private String photo;

}
