package jaega.homecare.domain.recurringOffer.service.query;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecommendRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.mapper.RecurringOfferMapper;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferQueryRepository;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import jaega.homecare.domain.recurringOffer.service.command.RecurringOfferCommandService;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecurringOfferQueryService {

    private final RecurringOfferMapper recurringOfferMapper;
    private final RecurringOfferRepository recurringOfferRepository;
    private final RecurringOfferQueryRepository recurringOfferQueryRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final RecurringOfferCommandService recurringOfferCommandService;
    private final ConsumerQueryService consumerQueryService;

    public RecurringOffer getRecurringOffer(UUID recurringOfferId){
        return recurringOfferRepository.findByRecurringOfferId(recurringOfferId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 정기 제안서 입니다"));
    }

    public GetRecurringOfferDetailResponse findRecurringOfferDetail(UUID recurringOfferId){
        RecurringOffer recurringOffer = getRecurringOffer(recurringOfferId);
        recurringOfferCommandService.readRecurringOfferDetail(recurringOffer);

        int durationInSeconds = calculateDurationInSeconds(
                recurringOffer.getServiceStartTime(),
                recurringOffer.getServiceEndTime()
        );

        return recurringOfferMapper.toGetResponseByDetail(recurringOffer, durationInSeconds);
    }

    public List<GetRecurringOfferResponse> findRecurringOfferByConsumer(UUID consumerId){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        List<RecurringOffer> recurringOfferList = recurringOfferRepository.findByConsumer(consumer);

        return recurringOfferList.stream()
                .map(offer -> {
                    int durationInSeconds = calculateDurationInSeconds(
                            offer.getServiceStartTime(),
                            offer.getServiceEndTime()
                    );

                    return recurringOfferMapper.toGetResponseByConsumer(offer, durationInSeconds);
                })
                .toList();
    }

    public List<GetRecommendRecurringOfferResponse> findRecommendedRecurringOffers(UUID consumerId) {
        List<ServiceMatch> matches = serviceMatchQueryRepository.findRecommendedServiceMatches(consumerId);

        return matches.stream()
                .map(sm -> new GetRecommendRecurringOfferResponse(
                        sm.getServiceMatchId(),
                        sm.getCaregiver().getCaregiverId(),
                        sm.getServiceDate(),
                        sm.getServiceStartTime(),
                        sm.getServiceEndTime(),
                        Set.of(sm.getServiceDate().getDayOfWeek())
                ))
                .toList();
    }

    public List<GetUnreadRecurringOfferResponse> findByUnreadRecurringOffers(UUID consumerId){
        List<RecurringOffer> recurringOfferList = recurringOfferQueryRepository.findUnreadRecurringOffersByConsumer(consumerId);

        return recurringOfferMapper.toGetResponseByUnreadNotification(recurringOfferList);
    }

    /**
     * 공통 duration 계산 메서드
     */
    private int calculateDurationInSeconds(LocalTime startTime, LocalTime endTime) {
        return (int) Duration.between(startTime, endTime).getSeconds();
    }
}
