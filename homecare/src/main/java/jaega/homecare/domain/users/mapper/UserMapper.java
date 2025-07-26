package jaega.homecare.domain.users.mapper;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "password", source = "request.password")
    @Mapping(target = "phone", source = "request.phone")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userRole", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserCreateRequest request);
}
