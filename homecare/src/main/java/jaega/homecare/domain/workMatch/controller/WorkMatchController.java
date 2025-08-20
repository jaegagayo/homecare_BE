package jaega.homecare.domain.workMatch.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.workMatch.dto.res.*;
import jaega.homecare.domain.workMatch.entity.WorkStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "WorkMatch", description = "근무 내역 일자 관련 조회 API")
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



    @Operation(summary = "근무 기록 조회 API", description = "근무 기록의 ID를 기반으로 정보를 조회합니다.")
    @ApiResponse(responseCode = "204", description = "근무 기록 ID 기반 조회 성공")
    @GetMapping
    ResponseEntity<GetWorkMatchResponse> getWorkMatch(@RequestParam UUID workMatchId);

    @Operation(summary = "특정 날짜의 근무 기록 조회 API", description = "특정 근무 날짜의 근무 기록들을 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 날짜의 근무 기록 조회 성공")
    @GetMapping("/workDay")
    ResponseEntity<List<GetWorkMatchByDateResponse>> getWorkMatchByWorkDay(@RequestParam UUID centerId, @RequestParam LocalDate workDate);

    @Operation(summary = "정산 상태 기반 근무 기록 조회 API", description = "정산 상태를 기반으로 특정 근무 기록들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 정산 상태 기반 근무 기록 조회 성공")
    @GetMapping("/paid")
    ResponseEntity<List<GetWorkMatchByPaid>> getWorkMatchByPaid(
            @RequestParam UUID centerId,
            @Parameter(
                    description = "정산 여부 (true: 정산 완료, false: 미정산)",
                    example = "true",
                    required = true
            )
            @RequestParam Boolean isPaid);
}
