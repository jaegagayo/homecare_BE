package jaega.homecare.domain.caregiverPreference.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.res.GetCaregiverPreferenceResponse;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CaregiverPreferenceMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "dayOfWeek", source = "request.dayOfWeek")
    @Mapping(target = "workStartTime", source = "request.workStartTime")
    @Mapping(target = "workEndTime", source = "request.workEndTime")
    @Mapping(target = "workMinTime", source = "request.workMinTime")
    @Mapping(target = "workMaxTime", source = "request.workMaxTime")
    @Mapping(target = "availableTime", source = "request.availableTime")
    @Mapping(target = "workArea", source = "request.workArea")
    @Mapping(target = "transportation", source = "request.transportation")
    @Mapping(target = "lunchBreak", source = "request.lunchBreak")
    @Mapping(target = "bufferTime", source = "request.bufferTime")
    @Mapping(target = "supportedConditions", source = "request.supportedConditions")
    @Mapping(target = "preferredMinAge", source = "request.preferredMinAge")
    @Mapping(target = "preferredMaxAge", source = "request.preferredMaxAge")
    @Mapping(target = "preferredGender", source = "request.preferredGender")
    @Mapping(target = "serviceTypes", source = "request.serviceTypes")
    @Mapping(target = "caregiverPreferenceId", ignore = true)
    CaregiverPreference toEntity(CreateCaregiverPreferenceRequest request, Caregiver caregiver);

    GetCaregiverPreferenceResponse toGetResponse(CaregiverPreference caregiverPreference);
}
