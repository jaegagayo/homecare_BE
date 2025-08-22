package jaega.homecare.domain.serviceMatch.controller;

import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/serviceMatch")
public class ServiceMatchControllerImpl implements ServiceMatchController {
    private final SettlementQueryService settlementQueryService;

    @Override
    public ResponseEntity<List<GetSettlementByDateResponse>> getServiceMatchByWorkDay(@RequestParam UUID centerId, @RequestParam LocalDate workDate) {
        List<GetSettlementByDateResponse> response = settlementQueryService.getSettlementByDate(centerId, workDate);
        return ResponseEntity.ok(response);
    }
}
