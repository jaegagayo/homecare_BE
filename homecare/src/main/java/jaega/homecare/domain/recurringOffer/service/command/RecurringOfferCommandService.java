package jaega.homecare.domain.recurringOffer.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.mapper.RecurringOfferMapper;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecurringOfferCommandService {

    private final RecurringOfferRepository recurringOfferRepository;
    private final RecurringOfferMapper recurringOfferMapper;
    private final CaregiverQueryService caregiverQueryService;
    private final ConsumerQueryService consumerQueryService;

    public void createRecurringOffer(CreateRecurringOfferRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        Consumer consumer = consumerQueryService.getConsumer(request.consumerId());

        RecurringOffer recurringOffer = recurringOfferMapper.toEntity(request, caregiver, consumer);
        recurringOffer.initializeRecurringOffer(UUID.randomUUID());
        recurringOfferRepository.save(recurringOffer);

    }

    public void readRecurringOfferDetail(RecurringOffer recurringOffer){
        recurringOffer.readRecurringOfferDetail();
    }
}
