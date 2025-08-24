package jaega.homecare.domain.recurringOffer.controller;

import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.RecommendRecurringOfferResponse;
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
    public ResponseEntity<List<GetRecurringOfferResponse>> getRecurringOfferByConsumer(@PathVariable UUID consumerId){
        List<GetRecurringOfferResponse> responses = recurringOfferQueryService.findRecurringOfferByConsumer(consumerId);
        return ResponseEntity.ok(responses);
    }

    // TODO : RequestParam인 이유 -> JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함
    @Override
    public ResponseEntity<List<RecommendRecurringOfferResponse>> getRecommendRecurringOfferForConsumer(@RequestParam UUID consumerId){
        List<RecommendRecurringOfferResponse> recurringOfferResponses = recurringOfferQueryService.getRecommendedRecurringOffers(consumerId);
        return ResponseEntity.ok(recurringOfferResponses);
    }

}
