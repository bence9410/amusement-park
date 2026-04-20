package hu.beni.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorResponseDto {

    private String email;

    private String authority;

    private Integer money;

    private Integer coupon;

    private String photo;

}
