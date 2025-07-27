package jaega.homecare.domain.serviceRequest.mapper;

import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.ConsumerServiceResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ServiceRequestMapper {

    @Mapping(target = "location", source = "request.location")
    @Mapping(target = "preferred_time_start", source = "request.preferred_time_start")
    @Mapping(target = "preferred_time_end", source = "request.preferred_time_end")
    @Mapping(target = "serviceType", source = "request.serviceType")
    @Mapping(target = "personalityType", source = "request.personalityType")
    @Mapping(target = "requestedDays", ignore = true)
    @Mapping(target = "serviceRequestId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    ServiceRequest toEntity(ConsumerServiceRequest request);

    @Mapping(target = "requestedDays", expression = "java(convertRequestedDays(request.getRequestedDays()))")
    ConsumerServiceResponse toFindResponseDto(ServiceRequest request);

    default String convertRequestedDays(Set<Integer> days) {
        if (days == null || days.isEmpty()) return "";
        return days.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}
