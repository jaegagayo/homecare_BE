package jaega.homecare.domain.recurringOffer.service.query;

import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.entity.RecurringOffer;
import jaega.homecare.domain.recurringOffer.mapper.RecurringOfferMapper;
import jaega.homecare.domain.recurringOffer.repository.RecurringOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecurringOfferQueryService {

    private final RecurringOfferMapper recurringOfferMapper;
    private final RecurringOfferRepository recurringOfferRepository;
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
}
