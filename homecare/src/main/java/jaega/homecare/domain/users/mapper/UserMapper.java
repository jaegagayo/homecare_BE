package jaega.homecare.domain.users.mapper;

import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserLoginResponse toLoginResponse(UUID userId);
}
