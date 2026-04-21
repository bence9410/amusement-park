package hu.bence.amusementpark.mapper;

import hu.bence.amusementpark.dto.request.UserSignUpRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.Photo;
import hu.bence.amusementpark.entity.Users;

public class UserMapper {

    public static UserResponseDto toDto(Users entity) {
        return UserResponseDto.builder()
                .name(entity.getName())
                .authority(entity.getAuthority())
                .money(entity.getMoney())
                .coupon(entity.getCoupon())
                .isActivatedCoupon(entity.isActivatedCoupon())
                .photo(entity.getPhoto().getPhoto()).build();
    }

    public static UserResponseDto toDtoWithoutPhoto(Users entity) {
        return UserResponseDto.builder()
                .name(entity.getName())
                .authority(entity.getAuthority())
                .money(entity.getMoney())
                .coupon(entity.getCoupon())
                .isActivatedCoupon(entity.isActivatedCoupon()).build();
    }

    public static Users toEntity(UserSignUpRequestDto dto) {
        return Users.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .dateOfBirth(dto.getDateOfBirth())
                .photo(Photo.builder().photo(dto.getPhoto()).build()).build();
    }
}
