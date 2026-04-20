package hu.bence.amusementpark.dto.request;

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
public class AmusementParkCreateRequestDto {

    @NotNull
    @Size(min = 5, max = 50)
    private String name;

    @NotNull
    @Range(min = 5, max = 200)
    private Integer entranceFee;
}
