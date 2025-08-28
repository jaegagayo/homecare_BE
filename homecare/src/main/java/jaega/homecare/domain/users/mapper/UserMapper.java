package jaega.homecare.domain.users.mapper;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import jaega.homecare.domain.users.dto.res.GetConsumerResponse;
import jaega.homecare.domain.users.dto.res.UserLoginResponse;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "phone", source = "request.phone")
    @Mapping(target = "birthDate", source = "request.birthDate")
    @Mapping(target = "gender", source = "request.gender")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userRole", ignore = true)
    User toEntity(UserCreateRequest request, String password);

    UserLoginResponse toLoginResponse(UUID userId);

    GetConsumerResponse toGetConsumerResponse(User user);
}
