package hu.bence.amusementpark.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private String name;

    private String authority;

    private Integer money;

    private Integer coupon;

    private String photo;

    private Boolean isActivatedCoupon;

}
