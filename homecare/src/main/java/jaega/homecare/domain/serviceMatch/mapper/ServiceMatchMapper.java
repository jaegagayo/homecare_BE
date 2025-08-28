package jaega.homecare.domain.serviceMatch.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.review.dto.res.ConsumerPendingReviewResponse;
import jaega.homecare.domain.review.dto.res.ReviewRequestResponse;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMatchMapper {

    @Mapping(target = "serviceRequest", source = "serviceRequest")
    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "serviceDate", source = "request.serviceDate")
    @Mapping(target = "serviceStartTime", source = "request.serviceStartTime")
    @Mapping(target = "serviceEndTime", source = "request.serviceEndTime")
    ServiceMatch toEntity(CreateServiceMatchRequest request, ServiceRequest serviceRequest, Caregiver caregiver);

    @Mapping(target = "consumerName", source = "serviceRequest.consumer.user.name")
    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    @Mapping(target = "serviceDate", source = "serviceDate")
    @Mapping(target = "serviceStartTime", source = "serviceStartTime")
    @Mapping(target = "serviceEndTime", source = "serviceEndTime")
    @Mapping(target = "matchStatus", source = "matchStatus")
    GetServiceMatchByUUID toGetResponseByUUID(ServiceMatch serviceMatch);

    @Mapping(target = "caregiverName", source = "caregiver.user.name")
    ConsumerPendingReviewResponse toConsumerPendingReviewResponse(ServiceMatch serviceMatch);

    List<ConsumerPendingReviewResponse> toConsumerPendingReviewResponse(List<ServiceMatch> serviceMatches);
}
