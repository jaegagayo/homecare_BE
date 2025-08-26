package jaega.homecare.domain.recurringOffer.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecurringOfferMapper {

    @Mapping(target = "caregiver", source = "caregiver")
    @Mapping(target = "consumer", source = "consumer")
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
}
