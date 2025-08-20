package jaega.homecare.domain.serviceRequest.mapper;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.req.LocationDto;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestById;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestResponse;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceRequestMapper {

    @Mapping(target = "consumer", source = "consumer")
    @Mapping(target = "serviceAddress", source = "request.serviceAddress")
    @Mapping(target = "addressType", source = "request.addressType")
    @Mapping(target = "location", expression = "java(map(request.location()))")
    @Mapping(target = "requestDate", source = "request.requestDate")
    @Mapping(target = "preferredStartTime", source = "request.preferredStartTime")
    @Mapping(target = "preferredEndTime", source = "request.preferredEndTime")
    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "serviceType", source = "request.serviceType")
    @Mapping(target = "additionalInformation", source = "request.additionalInformation")
    ServiceRequest toEntity(ConsumerServiceRequest request, Consumer consumer);

    GetServiceRequestResponse toFindResponseDto(ServiceRequest request);

    GetServiceRequestById toGetResponseById(ServiceRequest serviceRequest);

    GetCreateServiceResponse toGetCreateResponse(ServiceRequest serviceRequest);

    default Location map(LocationDto dto) {
        if (dto == null) return null;
        return new Location(dto.latitude(), dto.longitude());
    }
}
