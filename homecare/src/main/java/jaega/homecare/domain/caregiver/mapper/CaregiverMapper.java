package jaega.homecare.domain.caregiver.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.center.dto.res.GetCaregiverProfileResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CaregiverMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "phone", source = "request.phone")
    @Mapping(target = "birthDate", source = "request.birthDate")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userRole", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toUserEntity(CreateCaregiverRequest request, String password);

    @Mapping(target = "address", source = "address")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "center", source = "center")
    @Mapping(target = "caregiverId", ignore = true)
    @Mapping(target = "availableStartTime", ignore = true)
    @Mapping(target = "availableEndTime", ignore = true)
    @Mapping(target = "serviceTypes", ignore = true)
    @Mapping(target = "daysOff", ignore = true)
    Caregiver toEntity(String address, User user, Center center);

    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "email", source = "caregiver.user.email")
    @Mapping(target = "birthDate", source = "caregiver.user.birthDate")
    @Mapping(target = "phone", source = "caregiver.user.phone")
    GetCaregiverProfileResponse toGetCaregiverProfile(Caregiver caregiver);
}
