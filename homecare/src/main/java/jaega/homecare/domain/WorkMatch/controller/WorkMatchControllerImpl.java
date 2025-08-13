package jaega.homecare.domain.WorkMatch.controller;

import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverWorkResponse;
import jaega.homecare.domain.WorkMatch.dto.res.GetDailyUnsettledResponse;
import jaega.homecare.domain.WorkMatch.dto.res.GetMonthlyPaymentResponse;
import jaega.homecare.domain.WorkMatch.dto.res.GetSettlementSummaryResponse;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.WorkMatch.service.query.WorkMatchQueryService;
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
@RequestMapping("/api/workMatch")
public class WorkMatchControllerImpl implements WorkMatchController{
    private final WorkMatchQueryService workMatchQueryService;


    // 정산 페이지

    /**
     * 센터에 해당하는 요양보호사 근무 현황 조회
     */
    @Override
    public ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkList(
            @PathVariable UUID centerId,
            @RequestParam(required = false) WorkStatus status,
            @RequestParam(required = false) int year,
            @RequestParam(required = false) int month
    ) {
        List<GetCaregiverWorkResponse> result = workMatchQueryService.getCaregiverWorkList(centerId, status, year, month);
        return ResponseEntity.ok(result);
    }

    @Override
    public List<GetMonthlyPaymentResponse> getMonthlyPaid(@RequestParam UUID centerId) {
        return workMatchQueryService.getMonthlyPaidSettlements(centerId);
    }

    @Override
    public List<GetDailyUnsettledResponse> getDailyUnsettled(@RequestParam UUID centerId) {
        return workMatchQueryService.getDailyUnsettledCount(centerId);
    }

    @Override
    public GetSettlementSummaryResponse getSettlementSummary(@RequestParam UUID centerId) {
        return workMatchQueryService.getSettlementSummary(centerId);
    }
}
