package hu.bence.amusementpark.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyMoneyRequestDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String userName;

    @NotNull
    private Integer value;

}
