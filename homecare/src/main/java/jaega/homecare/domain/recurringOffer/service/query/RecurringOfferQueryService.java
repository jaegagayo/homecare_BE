package jaega.homecare.domain.recurringOffer.service.query;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.RecommendRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.mapper.RecurringOfferMapper;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecurringOfferQueryService {

    private final RecurringOfferMapper recurringOfferMapper;
    private final RecurringOfferRepository recurringOfferRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final ConsumerQueryService consumerQueryService;

    public RecurringOffer getRecurringOffer(UUID recurringOfferId){
        return recurringOfferRepository.findByRecurringOfferId(recurringOfferId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 정기 제안서 입니다"));
    }

    public List<GetRecurringOfferResponse> findRecurringOfferByConsumer(UUID consumerId){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        List<RecurringOffer> recurringOfferList = recurringOfferRepository.findByConsumer(consumer);
        return recurringOfferMapper.toGetResponseByConsumer(recurringOfferList);
    }

    public List<RecommendRecurringOfferResponse> getRecommendedRecurringOffers(UUID consumerId) {
        List<ServiceMatch> matches = serviceMatchQueryRepository.findRecommendedServiceMatches(consumerId);

        return matches.stream()
                .map(sm -> new RecommendRecurringOfferResponse(
                        sm.getServiceMatchId(),
                        sm.getCaregiver().getCaregiverId(),
                        sm.getServiceDate(),
                        sm.getServiceStartTime(),
                        sm.getServiceEndTime(),
                        Set.of(sm.getServiceDate().getDayOfWeek())
                ))
                .toList();
    }
}
