package hu.bence.amusementpark.mapper;

import hu.bence.amusementpark.dto.request.UserSignUpRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.Photo;
import hu.bence.amusementpark.entity.Users;

public class UserMapper {

    public static UserResponseDto toDto(Users entity) {
        return UserResponseDto.builder()
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .money(entity.getMoney())
                .coupon(entity.getCoupon())
                .photo(entity.getPhoto().getPhoto()).build();
    }

    public static UserResponseDto toDtoWithoutPhoto(Users entity) {
        return UserResponseDto.builder()
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .money(entity.getMoney())
                .coupon(entity.getCoupon()).build();
    }

    public static Users toEntity(UserSignUpRequestDto dto) {
        return Users.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .dateOfBirth(dto.getDateOfBirth())
                .photo(Photo.builder().photo(dto.getPhoto()).build()).build();
    }
}
