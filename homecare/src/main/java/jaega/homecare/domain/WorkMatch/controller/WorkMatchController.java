package jaega.homecare.domain.WorkMatch.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.WorkMatch.dto.res.*;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "WorkMatch", description = "정산 내역 일자 관련 조회 API")
@RequestMapping("/api/workMatch")
public interface WorkMatchController {

    @Operation(summary = "센터의 요양 보호사 정산 내역 조회", description = "센터에 등록된 요양 보호사들의 정산 내역을 조회합니다.")
    @ApiResponse(responseCode = "204", description = "센터에 등록된 요양 보호사들의 정산 내역을 조회 성공")
    @GetMapping(("/{centerId}/caregivers/work"))
    ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkList(
            @PathVariable UUID centerId,
            @RequestParam(required = false) WorkStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    );

    @Operation(summary = "센터의 요양 보호사 개별 정산 내역 조회", description = "센터의 요양 보호사 개별 정산 내역 조회합니다.")
    @ApiResponse(responseCode = "204", description = "센터의 요양 보호사 개별 정산 내역 조회 성공")
    @GetMapping(("/{caregiverId}/work"))
    ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkListByCaregiver(
            @PathVariable UUID caregiverId,
            @RequestParam(required = false) WorkStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    );

    @Operation(summary = "센터별 최근 6개월 총 정산 금액 조회", description = "센터의 최근 6개월 간 총 정산 금액을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 6개월 간 총 정산 금액 조회 성공")
    @GetMapping("/monthly")
    List<GetMonthlyPaymentResponse> getMonthlyPaid(@RequestParam UUID centerId);

    @Operation(summary = "센터별 최근 일주일 간 미정산 된 건수 조회", description = "센터의 최근 일주일 간 미정산 된 건수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 일주일 간 미정산 건 조회 성공")
    @GetMapping("/daily")
    List<GetDailyUnsettledResponse> getDailyUnsettled(@RequestParam UUID centerId);

    @Operation(summary = "센터의 정산 금액 및 정산 상태 통계 조회", description = "센터의 정산 금액 및 정산 상태 통계를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 정산 금액, 상태 통계 조회 성공")
    @GetMapping("/settlementSummary")
    GetSettlementCenterSummaryResponse getSettlementSummary(@RequestParam UUID centerId);

    @Operation(summary = "센터의 요양보호사 정산 금액 및 정산 상태 통계 조회", description = "센터의 요양보호사 정산 금액 및 정산 상태 통계 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 요양보호사 정산 금액 및 정산 상태 통계 조회 성공")
    @GetMapping("/caregiver/{caregiverId}/settlementSummary")
    ResponseEntity<GetCaregiverSettlementSummaryResponse> getCaregiverSettlementSummary(
            @PathVariable UUID caregiverId
    );
}
