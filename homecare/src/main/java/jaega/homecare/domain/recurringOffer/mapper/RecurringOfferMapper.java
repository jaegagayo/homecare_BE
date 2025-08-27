package jaega.homecare.domain.recurringOffer.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.req.LocationDto;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.users.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecurringOfferMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "consumer", source = "consumer")
    @Mapping(target = "serviceAddress", source = "request.serviceAddress")
    @Mapping(target = "addressType", source = "request.addressType")
    @Mapping(target = "location", expression = "java(map(request.location()))")
    @Mapping(target = "dayOfWeek", source = "request.dayOfWeek")
    @Mapping(target = "serviceStartDate", source = "request.serviceStartDate")
    @Mapping(target = "serviceEndDate", source = "request.serviceEndDate")
    @Mapping(target = "serviceStartTime", source = "request.serviceStartTime")
    @Mapping(target = "serviceEndTime", source = "request.serviceEndTime")
    @Mapping(target = "serviceType", source = "request.serviceType")
    @Mapping(target = "recurringOfferId", ignore = true)
    @Mapping(target = "recurringStatus", ignore = true)
    @Mapping(target = "recurringOfferUnread", ignore = true)
    RecurringOffer toEntity(CreateRecurringOfferRequest request, Caregiver caregiver, Consumer consumer);

    @Mapping(target = "caregiverId", source = "recurringOffer.caregiver.caregiverId")
    @Mapping(target = "consumerId", source = "recurringOffer.consumer.consumerId")
    @Mapping(target = "duration", source = "duration")
    GetRecurringOfferResponse toGetResponseByConsumer(RecurringOffer recurringOffer, int duration);

    @Mapping(target = "caregiverName", source = "recurringOffer.caregiver.user.name")
    @Mapping(target = "consumer", source = "recurringOffer.consumer.user.name")
    @Mapping(target = "duration", source = "duration")
    GetRecurringOfferDetailResponse toGetResponseByDetail(RecurringOffer recurringOffer, int duration);

    @Mapping(target = "caregiverName", source = "recurringOffer.caregiver.user.name")
    GetUnreadRecurringOfferResponse toGetResponseByUnreadNotification(RecurringOffer recurringOffer);

    List<GetUnreadRecurringOfferResponse> toGetResponseByUnreadNotification(List<RecurringOffer> recurringOffers);

    @Mapping(target = "consumerId", source = "recurringOffer.consumer.consumerId")
    @Mapping(target = "serviceAddress", source = "recurringOffer.serviceAddress")
    @Mapping(target = "addressType", source = "recurringOffer.addressType")
    @Mapping(target = "location", source = "recurringOffer.location")
    @Mapping(target = "requestDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "preferredStartTime", source = "recurringOffer.serviceStartTime")
    @Mapping(target = "preferredEndTime", source = "recurringOffer.serviceEndTime")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "serviceType", source = "recurringOffer.serviceType")
    @Mapping(target = "additionalInformation", constant = "null") // 필요시 변경
    ConsumerServiceRequest toConsumerServiceRequest(RecurringOffer recurringOffer, int duration);

    default LocationDto map(Location location) {
        if (location == null) return null;
        return new LocationDto(
                location.getLatitude(),
                location.getLongitude()
        );
    }

    default Location map(LocationDto dto) {
        if (dto == null) return null;
        return new Location(dto.latitude(), dto.longitude());
    }
}
