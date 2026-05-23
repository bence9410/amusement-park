package hu.bence.amusementpark.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequestDto {

    private String name;

    private String authority;

    private Integer minMoney;

    private Integer maxMoney;

    private Integer minCoupon;

    private Integer maxCoupon;

    private Boolean isActivatedCoupon;
}
