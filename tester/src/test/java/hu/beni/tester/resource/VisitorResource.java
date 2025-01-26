package hu.beni.tester.resource;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

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
