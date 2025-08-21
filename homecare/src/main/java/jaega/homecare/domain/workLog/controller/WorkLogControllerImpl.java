package jaega.homecare.domain.workLog.controller;

import jaega.homecare.domain.workLog.dto.res.*;
import jaega.homecare.domain.workLog.entity.WorkStatus;
import jaega.homecare.domain.workLog.service.query.WorkLogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workMatch")
public class WorkLogControllerImpl implements WorkLogController {
    private final WorkLogQueryService workLogQueryService;


    // 정산 페이지

    /**
     * 센터에 해당하는 요양보호사 근무 현황 조회
     */
    @Override
    public ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkList(
            @PathVariable UUID centerId,
            @RequestParam(required = false) WorkStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        List<GetCaregiverWorkResponse> result = workLogQueryService.getCaregiverWorkList(centerId, status, year, month);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkListByCaregiver(
            @PathVariable UUID caregiverId,
            @RequestParam(required = false) WorkStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        List<GetCaregiverWorkResponse> result = workLogQueryService.getCaregiverWorkListByCaregiver(caregiverId, status, year, month);
        return ResponseEntity.ok(result);
    }

    @Override
    public List<GetMonthlyPaymentResponse> getMonthlyPaid(@RequestParam UUID centerId) {
        return workLogQueryService.getMonthlyPaidSettlements(centerId);
    }

    @Override
    public List<GetDailyUnsettledResponse> getDailyUnsettled(@RequestParam UUID centerId) {
        return workLogQueryService.getDailyUnsettledCount(centerId);
    }

    @Override
    public GetSettlementCenterSummaryResponse getSettlementSummary(@RequestParam UUID centerId) {
        return workLogQueryService.getSettlementSummary(centerId);
    }

    @Override
    public ResponseEntity<GetCaregiverSettlementSummaryResponse> getCaregiverSettlementSummary(
            @PathVariable UUID caregiverId
    ) {
        return ResponseEntity.ok(workLogQueryService.getCaregiverSettlementSummary(caregiverId));
    }

    @Override
    public ResponseEntity<GetWorkLogResponse> getWorkLog(@PathVariable UUID workLogId) {
        GetWorkLogResponse responses = workLogQueryService.findWorkLog(workLogId);
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<GetWorkLogByDateResponse>> getWorkLogByWorkDay(@RequestParam UUID centerId, @RequestParam LocalDate workDate) {
        List<GetWorkLogByDateResponse> response = workLogQueryService.getWorkLogByDate(centerId, workDate);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetWorkLogByPaid>> getWorkLogByPaid(@RequestParam UUID centerId, Boolean isPaid) {
        List<GetWorkLogByPaid> response = workLogQueryService.getWorkLogByPaid(centerId, isPaid);
        return ResponseEntity.ok(response);
    }
}
