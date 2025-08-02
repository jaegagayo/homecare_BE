package jaega.homecare.domain.caregiverBlacklist.mapper;

import jaega.homecare.domain.caregiverBlacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.caregiverBlacklist.dto.res.GetCaregiverBlacklistResponse;
import jaega.homecare.domain.caregiverBlacklist.entity.CaregiverBlacklist;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CaregiverBlacklistMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "reporter", source = "reporter")
    @Mapping(target = "reason", source = "request.reason")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "blacklistId", ignore = true)
    CaregiverBlacklist toEntity(CreateCaregiverBlacklistRequest request, Caregiver caregiver, User reporter, LocalDateTime createdAt);

    @Mapping(target = "blacklistId", source = "blacklistId")
    @Mapping(target = "caregiverId", source = "caregiver.caregiverId")
    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "reporterId", source = "reporter.userId")
    @Mapping(target = "reporterName", source = "reporter.name")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "createdAt", source = "createdAt")
    GetCaregiverBlacklistResponse toGetResponse(CaregiverBlacklist caregiverBlacklist);
}