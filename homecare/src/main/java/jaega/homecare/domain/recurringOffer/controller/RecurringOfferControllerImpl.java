package jaega.homecare.domain.recurringOffer.controller;

import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecommendRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.service.command.RecurringOfferCommandService;
import jaega.homecare.domain.recurringOffer.service.query.RecurringOfferQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recurringOffer")
public class RecurringOfferControllerImpl implements RecurringOfferController{

    private final RecurringOfferQueryService recurringOfferQueryService;
    private final RecurringOfferCommandService recurringOfferCommandService;

    @Override
    public ResponseEntity<Void> createRecurringOffer(@RequestBody CreateRecurringOfferRequest request){
        recurringOfferCommandService.createRecurringOffer(request);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetRecurringOfferDetailResponse> getRecurringOfferDetail(@PathVariable UUID recurringOfferId){
        GetRecurringOfferDetailResponse response = recurringOfferQueryService.findRecurringOfferDetail(recurringOfferId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetRecurringOfferResponse>> getRecurringOfferByConsumer(@PathVariable UUID consumerId){
        List<GetRecurringOfferResponse> responses = recurringOfferQueryService.findRecurringOfferByConsumer(consumerId);
        return ResponseEntity.ok(responses);
    }

    // TODO : RequestParam인 이유 -> JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함
    @Override
    public ResponseEntity<List<GetRecommendRecurringOfferResponse>> getRecommendRecurringOffersForConsumer(@RequestParam UUID consumerId){
        List<GetRecommendRecurringOfferResponse> recurringOfferResponses = recurringOfferQueryService.findRecommendedRecurringOffers(consumerId);
        return ResponseEntity.ok(recurringOfferResponses);
    }

    // TODO : RequestParam인 이유 -> JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함
    @Override
    public ResponseEntity<List<GetUnreadRecurringOfferResponse>> getUnreadRecurringOffersForConsumer(@RequestParam UUID consumerId){
        List<GetUnreadRecurringOfferResponse> responses = recurringOfferQueryService.findByUnreadRecurringOffers(consumerId);
        return ResponseEntity.ok(responses);
    }

}
