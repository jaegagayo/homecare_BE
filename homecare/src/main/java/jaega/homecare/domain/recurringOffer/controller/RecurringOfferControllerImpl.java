package jaega.homecare.domain.recurringOffer.controller;

import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.*;
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

    /**
     *
     * 요양보호사 ( Caregiver )
     */

    @Override
    public ResponseEntity<List<GetCaregiverRecurringOfferSummaryResponse>> getRecurringOfferSummaryByCaregiver(@RequestParam UUID caregiverId){
        List<GetCaregiverRecurringOfferSummaryResponse> responses = recurringOfferQueryService.findByRecurringOfferSummaryByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<Void> approveRecurringStatus(@RequestBody UUID recurringStatusId){
        recurringOfferCommandService.approveRecurringStatus(recurringStatusId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> rejectRecurringStatus(@RequestParam UUID recurringStatusId){
        recurringOfferCommandService.rejectRecurringStatus(recurringStatusId);
        return ResponseEntity.noContent().build();
    }

}
