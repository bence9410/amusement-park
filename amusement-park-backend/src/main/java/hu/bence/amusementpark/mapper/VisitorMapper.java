package hu.bence.amusementpark.mapper;

import hu.bence.amusementpark.dto.request.VisitorSignUpRequestDto;
import hu.bence.amusementpark.dto.response.VisitorResponseDto;
import hu.bence.amusementpark.entity.Photo;
import hu.bence.amusementpark.entity.Visitor;

public class VisitorMapper {

    public static VisitorResponseDto toDto(Visitor entity) {
        return VisitorResponseDto.builder()
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .money(entity.getMoney())
                .coupon(entity.getCoupon())
                .photo(entity.getPhoto().getPhoto()).build();
    }

    public static VisitorResponseDto toDtoWithoutPhoto(Visitor entity) {
        return VisitorResponseDto.builder()
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .money(entity.getMoney())
                .coupon(entity.getCoupon()).build();
    }

    public static Visitor toEntity(VisitorSignUpRequestDto dto) {
        return Visitor.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .dateOfBirth(dto.getDateOfBirth())
                .photo(Photo.builder().photo(dto.getPhoto()).build()).build();
    }
}
