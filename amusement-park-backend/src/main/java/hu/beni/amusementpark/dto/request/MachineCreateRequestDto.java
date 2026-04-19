package hu.beni.amusementpark.dto.request;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.validation.constraint.EnumConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineCreateRequestDto {

    @NotNull
    @Size(min = 5, max = 25)
    private String fantasyName;

    @NotNull
    @Range(min = 20, max = 200)
    private Integer size;

    @NotNull
    @Range(min = 50, max = 2000)
    private Integer price;

    @NotNull
    @Range(min = 5, max = 250)
    private Integer numberOfSeats;

    @NotNull
    @Range(max = 21)
    private Integer minimumRequiredAge;

    @NotNull
    @Range(min = 5, max = 30)
    private Integer ticketPrice;

    @EnumConstraint(enumClazz = MachineType.class, message = "must be one of [CAROUSEL, ROLLER_COASTER, GOKART, DODGEM, SHIP]")
    private String type;

}
