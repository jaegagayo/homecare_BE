package jaega.homecare.domain.center.controller;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.dto.req.CreateCaregiverCenterRequest;
import jaega.homecare.domain.caregiverCenter.service.command.CaregiverCenterCommandService;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jaega.homecare.domain.serviceMatch.dto.res.CenterScheduleDetailResponse;
import jaega.homecare.domain.serviceMatch.dto.res.CenterScheduleResponse;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.dto.res.GetDashboardPopularResponse;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.service.command.CertificationCommandService;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiver.service.query.CertificationQueryService;
import jaega.homecare.domain.center.dto.req.*;
import jaega.homecare.domain.center.dto.res.*;
import jaega.homecare.domain.center.service.command.CenterCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/center")
public class CenterControllerImpl implements CenterController{

    private final CenterCommandService centerCommandService;
    private final CenterQueryService centerQueryService;
    private final CaregiverCenterCommandService caregiverCenterCommandService;
    private final CaregiverQueryService caregiverQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final SettlementQueryService settlementQueryService;
    private final CertificationCommandService certificationCommandService;
    private final CertificationQueryService certificationQueryService;


    // 센터 로그인
    @Override
    public ResponseEntity<CenterLoginResponse> loginCenter(@RequestBody CenterLoginRequest request){
        CenterLoginResponse response = centerCommandService.loginCenterWithoutAuth();
        return ResponseEntity.ok(response);
    }


    /**
     *
     * 요양보호사 관련 API
     */

    // 요양보호사 검색
    @Override
    public ResponseEntity<List<SearchCaregiverResponse>> searchCaregiver(@RequestParam String keyword){
        List<SearchCaregiverResponse> responses = caregiverQueryService.searchCaregiver(keyword);
        return ResponseEntity.ok(responses);
    }

    // 요양보호사 기관 등록
    @Override
    public ResponseEntity<Void> registerCaregiver(@RequestBody CreateCaregiverCenterRequest request){
        Caregiver caregiver = caregiverQueryService.getCaregiver(request.caregiverId());
        Center center = centerQueryService.findCenterByUUID(request.centerId());
        caregiverCenterCommandService.createCaregiverCenter(center, caregiver);
        return ResponseEntity.noContent().build();
    }

    // 요양보호사 전체 목록 조회
    @Override
    public ResponseEntity<List<GetCaregiverResponse>> getAllCaregiver(@PathVariable UUID centerId){
        List<GetCaregiverResponse> caregivers = caregiverQueryService.getAllCaregiversByCenter(centerId);
        return ResponseEntity.ok(caregivers);
    }

    // 요양보호사 인사카드 조회 (상세 조회)
    @Override
    public ResponseEntity<GetCaregiverProfileResponseByCenter> getCaregiverProfile(@RequestParam UUID caregiverId){
        GetCaregiverProfileResponseByCenter response = caregiverQueryService.getCaregiverProfileByCenter(caregiverId);
        return ResponseEntity.ok(response);
    }

    // 요양보호사 근무 상태 기반 조회
    @Override
    public ResponseEntity<List<GetCaregiverByStatusResponse>> getCaregiverByWorkStatus(@PathVariable UUID centerId,
                                                                                       @RequestParam CaregiverStatus caregiverStatus) {
        List<GetCaregiverByStatusResponse> response = caregiverQueryService.getCaregiverByWorkStatus(centerId, caregiverStatus);
        return ResponseEntity.ok(response);
    }

    // 요양보호사 서비스 유형 기반 조회
    @Override
    public ResponseEntity<List<GetCaregiverByServiceTypeResponse>> getCaregiverByServiceType(@RequestParam UUID centerId,
                                                                                             @RequestParam Set<ServiceType> serviceType){
        List<GetCaregiverByServiceTypeResponse> responses = caregiverQueryService.getCaregiverByServiceType(centerId, serviceType);
        return ResponseEntity.ok(responses);
    }

    // 요양보호사 말소
    @Override
    public ResponseEntity<Void> deregisterCaregiver(UUID centerId, UUID caregiverId) {
        centerCommandService.deregisterCaregiver(centerId, caregiverId);
        return ResponseEntity.noContent().build();
    }



    /**
     *
     * 일정 조회 API
     */

    // 일정 전체 조회
    public ResponseEntity<List<CenterScheduleResponse>> getAllSchedule(
            @RequestParam UUID centerId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ){
        List<CenterScheduleResponse> response = serviceMatchQueryService.getCenterSchedule(centerId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // 특정 날짜에서의 요양보호사 일정 조회
    @Override
    public ResponseEntity<List<GetCaregiverMatchesByMonth>> getScheduleByDate(
            @RequestParam UUID centerId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer day
    ) {
        List<GetCaregiverMatchesByMonth> response = serviceMatchQueryService.getMatchesByMonth(centerId, year, month, day);
        return ResponseEntity.ok(response);
    }

    // 센터의 요양보호사 별 일정 조회
    @Override
    public ResponseEntity<List<GetCaregiverMatchesResponse>> getScheduleByCaregiver(@PathVariable UUID caregiverId) {
        List<GetCaregiverMatchesResponse> responses = serviceMatchQueryService.getMatchesByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }

    // 일정 상세 조회
    @Override
    public ResponseEntity<CenterScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id){
        CenterScheduleDetailResponse response = serviceMatchQueryService.getMatchesByUUID(id);
        return ResponseEntity.ok(response);
    }



    /**
     *
     * 자격증 관련 API
     */

    // 요양보호사의 자격증 조회
    @Override
    public ResponseEntity<GetCertificationResponse> getCertificationByCaregiver(@PathVariable UUID caregiverId){
        GetCertificationResponse response = certificationQueryService.getCertificationByCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

    // 요양보호사의 자격증 교육 상태 전환
    @Override
    public ResponseEntity<Void> changeTrainStatus(@RequestBody UUID certificationId){
        certificationCommandService.changeTrainStatus(certificationId);
        return ResponseEntity.noContent().build();
    }



    /**
     *
     * 정산 관련 API
     */

    // 센터의 정산 내역 조회
    @Override
    public ResponseEntity<List<GetSettlementResponse>> getSettlement(
            @RequestParam UUID centerId,
            @RequestParam MatchStatus status,
            @RequestParam LocalDate date
    ){
        List<GetSettlementResponse> responses = settlementQueryService.getCenterSettlement(centerId, status, date);
        return ResponseEntity.ok(responses);
    }

    // 센터의 총 정산 금액 및 정산 상태 통계 조회
    @Override
    public GetSettlementSummaryResponse getSettlementSummary(@RequestParam UUID centerId){
        return settlementQueryService.getCenterSettlementSummary(centerId);
    }

    // 요양보호사 별 정산 내역 조회
    @Override
    public ResponseEntity<List<GetSettlementResponse>> getSettlementByCaregiver(
            @PathVariable UUID caregiverId,
            @RequestParam UUID centerId,
            @RequestParam MatchStatus status,
            @RequestParam LocalDate date
            ){
        List<GetSettlementResponse> result = settlementQueryService.getSettlementByCaregiver(centerId, caregiverId, status, date);
        return ResponseEntity.ok(result);
    }

    // 요양보호사 총 정산 금액 및 정산 상태 통계 조회
    @Override
    public ResponseEntity<GetSettlementSummaryByCaregiverResponse> getSettlementSummaryByCaregiver(@PathVariable UUID caregiverId){
        return ResponseEntity.ok(settlementQueryService.getCaregiverSettlementSummary(caregiverId));
    }

    // 최근 6개월 간 총 정산 금액 조회
    @Override
    public List<GetMonthlyPaymentResponse> getMonthlySettlement(@RequestParam UUID centerId){
        return settlementQueryService.getMonthlyPaidSettlements(centerId);
    }

    // 최근 일주일 간 미정산 된 건수 조회
    @Override
    public List<GetDailyUnsettledResponse> getWeeklyUnsettled(@RequestParam UUID centerId){
        return settlementQueryService.getWeeklyUnsettledCount(centerId);
    }

    // 정산 상태 기반 정산 내역 조회 API
    @Override
    public ResponseEntity<List<GetSettlementByPaid>> getSettlementByPaid(@RequestParam UUID centerId, @RequestParam Boolean isPaid){
        List<GetSettlementByPaid> response = settlementQueryService.getSettlementByPaid(centerId, isPaid);
        return ResponseEntity.ok(response);
    }


    /**
     *
     * 대시보드 관련 API
     */

    // (대시보드) 요양보호사 인구 현황 조회
    @Override
    public ResponseEntity<GetDashboardPopularResponse> getDashboardPopular(@RequestParam UUID centerId){
        GetDashboardPopularResponse response = caregiverQueryService.getCaregiverStats(centerId);
        return ResponseEntity.ok(response);
    }

    // (대시보드) 정산 현황 조회
    @Override
    public ResponseEntity<GetDashboardSettlementResponse> getDashboardSettlement(@RequestParam UUID centerId) {
        GetDashboardSettlementResponse response = settlementQueryService.getSettlementStatus(centerId);
        return ResponseEntity.ok(response);
    }

    // (대시보드) 근무 현황 조회
    @Override
    public ResponseEntity<GetDashboardWorkStatusResponse> getDashboardWorkStatus(@RequestParam UUID centerId){
        GetDashboardWorkStatusResponse response = serviceMatchQueryService.getDashboardWorkStatus(centerId);
        return ResponseEntity.ok(response);
    }
}
