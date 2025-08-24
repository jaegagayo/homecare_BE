package jaega.homecare.domain.recurringOffer.controller;

import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.service.command.RecurringOfferCommandService;
import jaega.homecare.domain.recurringOffer.service.query.RecurringOfferQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
