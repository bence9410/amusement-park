package hu.beni.amusementpark.dto.request;

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
    @Size(min = 5, max = 50)
    private String fantasyName;

    @NotNull
    @Range(max = 21)
    private Integer minimumRequiredAge;

    @NotNull
    @Range(min = 1, max = 30)
    private Integer ticketPrice;

    @NotNull
    @Size(min = 3)
    private String video;

    @NotNull
    @Range(min = 5, max = 300)
    private Integer videoLengthInSeconds;
}
