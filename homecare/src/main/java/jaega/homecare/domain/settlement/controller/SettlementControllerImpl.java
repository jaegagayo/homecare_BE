package jaega.homecare.domain.settlement.controller;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/settlement")
public class SettlementControllerImpl implements SettlementController {
    private final SettlementQueryService settlementQueryService;

    @Override
    public ResponseEntity<GetSettlementResponse> getSettlement(@PathVariable UUID settlementId) {
        GetSettlementResponse responses = settlementQueryService.findSettlement(settlementId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 센터에 해당하는 요양보호사 근무 현황 조회
     */
    @Override
    public ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkList(
            @PathVariable UUID centerId,
            @RequestParam(required = false) MatchStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        List<GetCaregiverWorkResponse> result = settlementQueryService.getCaregiverWorkList(centerId, status, year, month);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkListByCaregiver(
            @PathVariable UUID caregiverId,
            @RequestParam(required = false) MatchStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        List<GetCaregiverWorkResponse> result = settlementQueryService.getCaregiverWorkListByCaregiver(caregiverId, status, year, month);
        return ResponseEntity.ok(result);
    }

    // 센터의 최근 6개월 간 정산 내역 조회
    @Override
    public List<GetMonthlyPaymentResponse> getMonthlyPaid(@RequestParam UUID centerId) {
        return settlementQueryService.getMonthlyPaidSettlements(centerId);
    }

    // 센터의 최근 일주일간 정산 내역 조회
    @Override
    public List<GetDailyUnsettledResponse> getDailyUnsettled(@RequestParam UUID centerId) {
        return settlementQueryService.getDailyUnsettledCount(centerId);
    }

    // 얜 필요 없을 것 같은데?
    @Override
    public GetSettlementCenterSummaryResponse getSettlementSummary(@RequestParam UUID centerId) {
        return settlementQueryService.getSettlementSummary(centerId);
    }

    // 확정이 된 애들 중에서 정산이 된 / 아닌 애들만 보여주면 될 것 같은데?
    @Override
    public ResponseEntity<GetCaregiverSettlementSummaryResponse> getCaregiverSettlementSummary(
            @PathVariable UUID caregiverId
    ) {
        return ResponseEntity.ok(settlementQueryService.getCaregiverSettlementSummary(caregiverId));
    }


    @Override
    public ResponseEntity<List<GetSettlementByPaid>> getSettlementByPaid(@RequestParam UUID centerId, Boolean isPaid) {
        List<GetSettlementByPaid> response = settlementQueryService.getSettlementByPaid(centerId, isPaid);
        return ResponseEntity.ok(response);
    }
}
