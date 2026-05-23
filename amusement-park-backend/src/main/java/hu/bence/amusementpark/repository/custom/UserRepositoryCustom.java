package hu.bence.amusementpark.repository.custom;

import hu.bence.amusementpark.dto.request.UserSearchRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    Page<UserResponseDto> findAll(UserSearchRequestDto dto, Pageable pageable);

}
