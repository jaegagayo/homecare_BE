package jaega.homecare.domain.users.mapper;

import jaega.homecare.domain.users.dto.res.GetConsumerResponse;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserLoginResponse toLoginResponse(UUID userId);

    GetConsumerResponse toGetConsumerResponse(User user);
}
