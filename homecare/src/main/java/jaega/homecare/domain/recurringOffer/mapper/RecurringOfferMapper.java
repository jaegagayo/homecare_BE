package jaega.homecare.domain.recurringOffer.mapper;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
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
    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "serviceType", source = "request.serviceType")
    @Mapping(target = "recurringOfferId", ignore = true)
    @Mapping(target = "recurringStatus", ignore = true)
    RecurringOffer toEntity(CreateRecurringOfferRequest request, Caregiver caregiver, Consumer consumer);

    @Mapping(target = "caregiverId", source = "caregiver.caregiverId")
    @Mapping(target = "consumerId", source = "consumer.consumerId")
    GetRecurringOfferResponse toGetResponseByConsumer(RecurringOffer recurringOffer);

    List<GetRecurringOfferResponse> toGetResponseByConsumer(List<RecurringOffer> recurringOffers);
}
