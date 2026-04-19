package hu.beni.amusementpark.mapper;

import hu.beni.amusementpark.dto.request.VisitorSignUpRequestDto;
import hu.beni.amusementpark.dto.response.VisitorResponseDto;
import hu.beni.amusementpark.entity.Photo;
import hu.beni.amusementpark.entity.Visitor;

public class VisitorMapper {

    public static VisitorResponseDto toDto(Visitor entity) {
        return VisitorResponseDto.builder()
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .spendingMoney(entity.getSpendingMoney())
                .photo(entity.getPhoto().getPhoto()).build();
    }

    public static Visitor toEntity(VisitorSignUpRequestDto dto) {
        return Visitor.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .dateOfBirth(dto.getDateOfBirth())
                .photo(Photo.builder().photo(dto.getPhoto()).build()).build();
    }
}
