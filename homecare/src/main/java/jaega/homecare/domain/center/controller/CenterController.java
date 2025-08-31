package jaega.homecare.domain.center.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.caregiverCenter.dto.req.CreateCaregiverCenterRequest;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.serviceMatch.dto.res.CenterScheduleDetailResponse;
import jaega.homecare.domain.serviceMatch.dto.res.CenterScheduleResponse;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.dto.res.GetDashboardPopularResponse;
import jaega.homecare.domain.center.dto.req.*;
import jaega.homecare.domain.center.dto.res.*;
import jaega.homecare.domain.users.entity.ServiceType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Center", description = "Center 서비스 API")
@RequestMapping("/api/center")
public interface CenterController {

    @Operation(summary = "센터 로그인 API", description = "입력받은 정보로 센터의 로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "센터 로그인 성공")
    @PostMapping("/login")
    ResponseEntity<CenterLoginResponse> loginCenter(@RequestBody CenterLoginRequest request);


    /**
     *
     * 요양보호사 관련 API
     */

    @Operation(summary = "요양 보호사 검색 API", description = "센터에 등록하기 위해 이름, 전화번호 중 하나로 요양보호사를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "요양 보호사 검색 성공")
    @GetMapping("/caregiver/search")
    ResponseEntity<List<SearchCaregiverResponse>> searchCaregiver(
            @RequestParam String keyword
    );

    @Operation(summary = "센터의 요양보호사 기관 등록 API", description = "해당 요양보호사를 기관에 등록합니다.")
    @ApiResponse(responseCode = "204", description = "요양 보호사 등록 성공")
    @PostMapping("/caregiver/register")
    ResponseEntity<Void> registerCaregiver(@RequestBody CreateCaregiverCenterRequest request);

    @Operation(summary = "요양보호사 전체 목록 조회 API", description = "센터에 소속된 요양 보호사를 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양 보호사 전체 조회 성공")
    @GetMapping("/caregiver")
    ResponseEntity<List<GetCaregiverResponse>> getAllCaregiver(@RequestParam UUID centerId);

    @Operation(summary = "요양보호사 인사카드 조회 (상세 조회) API", description = "요양보호사의 ID로 인사카드를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사 인사카드 조회 성공")
    @GetMapping("/caregiver/{caregiverId}")
    ResponseEntity<GetCaregiverProfileResponse> getCaregiverProfile(@PathVariable UUID caregiverId);

    @Operation(summary = "요양보호사 근무 상태 기반 조회 API", description = "센터의 요양보호사들의 근무 상태(근무 중, 휴직, ...)를 기반으로 요양보호사 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 근무 상태 기반 목록 조회 성공")
    @GetMapping("/caregiver/status")
    ResponseEntity<List<GetCaregiverByStatusResponse>> getCaregiverByWorkStatus(@RequestParam UUID centerId, @RequestParam CaregiverStatus status);

    @Operation(summary = "요양보호사 서비스 유형 기반 조회 API", description = "센터 요양보호사의 서비스 유형 상태(재가, 방문, ...)를 기반으로 요양보호사 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 서비스 유형 기반 조회 성공")
    @GetMapping("/caregiver/service-type")
    ResponseEntity<List<GetCaregiverByServiceTypeResponse>> getCaregiverByServiceType(@RequestParam UUID centerId, @RequestParam Set<ServiceType> serviceType);

    @Operation(summary = "요양 보호사 말소 API", description = "입력받은 센터 ID와 보호사 ID를 이용하여 해당 요양 보호사를 말소(삭제)합니다.")
    @ApiResponse(responseCode = "204", description = "요양 보호사 말소 성공")
    @DeleteMapping("/caregiver/{caregiverId}")
    ResponseEntity<Void> deregisterCaregiver(@RequestParam UUID centerId, @PathVariable UUID caregiverId);


    /**
     *
     * 일정 조회 API
     */

    @Operation(summary = "일정 전체 조회", description = "센터의 일정을 전체 조회합니다")
    @ApiResponse(responseCode = "200", description = "센터의 전체 일정 조회 성공")
    @GetMapping("/schedule")
    ResponseEntity<List<CenterScheduleResponse>> getAllSchedule(
            @RequestParam UUID centerId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    );

    @Operation(summary = "특정 날짜에서의 요양보호사 일정 조회", description = "특정 년도, 월(필수), 일(선택) 의 요양보호사 매칭 스케줄 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 년도, 월, 일의 요양보호사 매칭 스케줄 조회")
    @GetMapping("/schedule/date")
    ResponseEntity<List<GetCaregiverMatchesByMonth>> getScheduleByDate(
            @RequestParam UUID centerId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer day
    );

    @Operation(summary = "센터의 요양보호사 별 일정 조회", description = "특정 요양 보호사의 일정을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 요양 보호사의 매칭 스케줄 조회 성공")
    @GetMapping("/schedule/{caregiverId}")
    ResponseEntity<List<GetCaregiverMatchesResponse>> getScheduleByCaregiver(@PathVariable UUID caregiverId);

    @Operation(summary = "일정 상세 조회 API", description = "센터에서 서비스 매칭 UUID를 기반으로 일정 정보를 상세 조회합니다.<br>" +
                                                        "센터에서 1. 배정 내역 전체 조회를 하고 리턴된 UUID를 기반으로 이 API를 호출하는 느낌으로 구현했습니다.")
    @ApiResponse(responseCode = "200", description = "특정 서비스 정보 조회 API")
    @GetMapping("/schedule/{id}")
    ResponseEntity<CenterScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id);


    /**
     *
     * 자격증 관련 API
     */

    @Operation(summary = "요양보호사의 자격증 조회 API", description = "센터 요양보호사의 자격증을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 자격증 정보 조회 성공")
    @GetMapping("/certification")
    ResponseEntity<GetCertificationResponse> getCertificationByCaregiver(@RequestParam UUID caregiverId);

    @Operation(summary = "요양보호사 자격증 교육 상태 전환 API", description = "요양보호사 자격증의 교육 완료 상태를 전환합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사 자격증의 교육 상태 전환 성공")
    @PostMapping("/certification/change")
    ResponseEntity<Void> changeTrainStatus(@RequestBody UUID certificationId);

    /**
     *
     * 정산 관련 API
     */

    @Operation(summary = "정산 내역 전체 조회 API", description = "센터의 전체 정산 내역을 조회합니다.")
    @ApiResponse(responseCode = "204", description = "정산 내역 상세 조회 성공")
    @GetMapping("/settlement")
    ResponseEntity<List<GetSettlementResponse>> getSettlement(
            @RequestParam UUID centerId,
            @RequestParam MatchStatus status,
            @RequestParam LocalDate date
    );

    @Operation(summary = "센터의 총 정산 금액 및 정산 상태 통계 조회", description = "센터의 정산 금액 및 정산 상태 통계를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 정산 금액, 상태 통계 조회 성공")
    @GetMapping("/settlement/summary")
    GetSettlementSummaryResponse getSettlementSummary(@RequestParam UUID centerId);

    @Operation(summary = "요양보호사 별 정산 내역 조회", description = "센터에 등록된 요양 보호사들의 정산 내역을 조회합니다.")
    @ApiResponse(responseCode = "204", description = "센터에 등록된 요양 보호사들의 정산 내역을 조회 성공")
    @GetMapping(("/settlement/{caregiverId}"))
    ResponseEntity<List<GetSettlementResponse>> getSettlementByCaregiver(
            @PathVariable UUID caregiverId,
            @RequestParam UUID centerId,
            @RequestParam MatchStatus status,
            @RequestParam LocalDate date
    );

    @Operation(summary = "요양보호사 총 정산 금액 및 정산 상태 통계 조회", description = "센터의 요양보호사 정산 금액 및 정산 상태 통계 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 요양보호사 정산 금액 및 정산 상태 통계 조회 성공")
    @GetMapping("/settlement/{caregiverId}/summary")
    ResponseEntity<GetSettlementSummaryByCaregiverResponse> getSettlementSummaryByCaregiver(@PathVariable UUID caregiverId);

    @Operation(summary = "최근 6개월 총 정산 금액 조회", description = "센터의 최근 6개월 간 총 정산 금액을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 6개월 간 총 정산 금액 조회 성공")
    @GetMapping("/settlement/monthly")
    List<GetMonthlyPaymentResponse> getMonthlySettlement(@RequestParam UUID centerId);

    @Operation(summary = "최근 일주일 간 미정산 된 건수 조회", description = "센터의 최근 일주일 간 미정산 된 건수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터의 일주일 간 미정산 건 조회 성공")
    @GetMapping("/settlement/weekly")
    List<GetDailyUnsettledResponse> getWeeklyUnsettled(@RequestParam UUID centerId);

    @Operation(summary = "정산 상태 기반 정산 내역 조회 API", description = "정산 상태를 기반으로 특정 정산 내역들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 정산 상태 기반 정산 내역 조회 성공")
    @GetMapping("/settlement/status")
    ResponseEntity<List<GetSettlementByPaid>> getSettlementByPaid(
            @RequestParam UUID centerId,
            @Parameter(
                    description = "정산 여부 (true: 정산 완료, false: 미정산)",
                    example = "true",
                    required = true
            )
            @RequestParam Boolean isPaid);


    /**
     *
     * 대시보드 조회 API
     */


    @Operation(summary = "센터 대시보드의 요양보호사 인구 현황 조회", description = "센터 대시보드에서 요양보호사 인구 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 대시보드에 요양보호사 인구 현황 조회 성공")
    @GetMapping("/dashboard/popular")
    ResponseEntity<GetDashboardPopularResponse> getDashboardPopular(@RequestParam UUID centerId);

    @Operation(summary = "센터 대시보드의 정산 현황 조회", description = "센터 대시보드에서 정산 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 대시보드에 정산 현황 조회 성공")
    @GetMapping("/dashboard/settlement")
    ResponseEntity<GetDashboardSettlementResponse> getDashboardSettlement(@RequestParam UUID centerId);

    @Operation(summary = "센터 대시보드의 근무 현황 조회", description = "센터 대시보드에서 근무 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 대시보드에 근무 현황 조회 성공")
    @GetMapping("/dashboard/workStatus")
    ResponseEntity<GetDashboardWorkStatusResponse> getDashboardWorkStatus(@RequestParam UUID centerId);

//    @Operation(summary = "배정 내역 전체 조회 API", description = "배정된 신청자-요양보호사 전체 목록을 최신순으로 조회합니다.")
//    @ApiResponse(responseCode = "200", description = "요양 보호사 전체 조회 성공")
//    @GetMapping("/center/notification/confirmed")
//    ResponseEntity<List<GetServiceMatchByCenterResponse>> getAllMatchingResult(@RequestParam UUID centerId);
}
