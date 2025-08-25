package jaega.homecare.domain.caregiver.mapper;

import jaega.homecare.domain.caregiver.dto.req.CaregiverCreateRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverSignupResponse;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverVerifiedStatusResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.center.dto.res.GetCaregiverProfileResponse;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CaregiverMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "availableStartTime", source = "request.availableStartTime")
    @Mapping(target = "availableEndTime", source = "request.availableEndTime")
    @Mapping(target = "address", source = "request.address")
    @Mapping(target = "career", source = "request.career")
    @Mapping(target = "koreanProficiency", source = "request.koreanProficiency")
    @Mapping(target = "isAccompanyOuting", source = "request.isAccompanyOuting")
    @Mapping(target = "selfIntroduction", source = "request.selfIntroduction")
    @Mapping(target = "verifiedStatus", source = "request.verifiedStatus")
    @Mapping(target = "caregiverId", ignore = true)
    Caregiver toEntity(CaregiverCreateRequest request, User user);

    @Mapping(target = "caregiverId", source = "caregiverId")
    GetCaregiverSignupResponse toGetCaregiverSignup(Caregiver caregiver);

    @Mapping(target = "caregiverId", source = "caregiverId")
    @Mapping(target = "verifiedStatus", source = "verifiedStatus")
    GetCaregiverVerifiedStatusResponse toGetCaregiverVerifiedStatus(Caregiver caregiver);

    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "email", source = "caregiver.user.email")
    @Mapping(target = "birthDate", source = "caregiver.user.birthDate")
    @Mapping(target = "phone", source = "caregiver.user.phone")
    @Mapping(target = "serviceTypes", source = "preference.serviceTypes")
    GetCaregiverProfileResponse toGetCaregiverProfile(Caregiver caregiver, CaregiverPreference preference);
}
