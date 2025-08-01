package jaega.homecare.domain.serviceMatch.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ServiceMatchMapper {

    @Mapping(target = "serviceRequest", source = "serviceRequest")
    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "serviceDate", source = "serviceDate")
    @Mapping(target = "startTime", source = "request.serviceTime_start")
    @Mapping(target = "endTime", source = "request.serviceTime_end")
    @Mapping(target = "serviceMatchId", ignore = true)
    @Mapping(target = "status", ignore = true)
    ServiceMatch toEntity(CreateServiceMatchRequest request, LocalDate serviceDate, ServiceRequest serviceRequest, Caregiver caregiver);

    @Mapping(target = "consumerName", source = "serviceRequest.user.name")
    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "serviceDate", source = "serviceDate")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "status", source = "status")
    GetServiceMatchByUUID toGetResponseByUUID(ServiceMatch serviceMatch);
}
