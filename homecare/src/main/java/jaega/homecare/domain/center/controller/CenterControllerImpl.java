package jaega.homecare.domain.center.controller;

import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.settlement.dto.res.GetDashboardSettlementResponse;
import jaega.homecare.domain.settlement.dto.res.GetDashboardWorkStatusResponse;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import jaega.homecare.domain.caregiver.dto.req.CreateCertificationRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.dto.res.GetDashboardPopularResponse;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.service.command.CaregiverCommandService;
import jaega.homecare.domain.caregiver.service.command.CertificationCommandService;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiver.service.query.CertificationQueryService;
import jaega.homecare.domain.center.dto.req.*;
import jaega.homecare.domain.center.dto.res.*;
import jaega.homecare.domain.center.service.command.CenterCommandService;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/center")
public class CenterControllerImpl implements CenterController{

    private final CenterCommandService centerCommandService;
    private final CaregiverCommandService caregiverCommandService;
    private final CaregiverQueryService caregiverQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final SettlementQueryService settlementQueryService;
    private final CertificationCommandService certificationCommandService;
    private final CertificationQueryService certificationQueryService;


    /**
     *
     * 센터 로그인 API
     */
    @Override
    public ResponseEntity<CenterLoginResponse> loginCenter(@RequestBody CenterLoginRequest request){
        CenterLoginResponse response = centerCommandService.loginCenterWithoutAuth();
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 요양보호사 등록 API
     */
    @Override
    public ResponseEntity<Void> registerCaregiver(@RequestBody CreateCaregiverRequest createCaregiverRequest, @PathVariable UUID centerId){
        centerCommandService.registerCaregiver(createCaregiverRequest, UserRole.ROLE_CAREGIVER, centerId);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 요양 보호사 말소 API
     */
    @Override
    public ResponseEntity<Void> deregisterCaregiver(UUID centerId, UUID caregiverId) {
        centerCommandService.deregisterCaregiver(centerId, caregiverId);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 보호사 상세 정보 등록 API
     */
    @Override
    public ResponseEntity<Void> createCaregiverProfile(@RequestBody CreateCaregiverProfileRequest request){
        caregiverCommandService.createCaregiverProfile(request);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 센터의 요양보호사 목록 조회 API
     */
    @Override
    public ResponseEntity<List<GetCaregiverResponse>> getAllCaregivers(@PathVariable UUID centerId){
        List<GetCaregiverResponse> caregivers = caregiverQueryService.getAllCaregiversByCenter(centerId);
        return ResponseEntity.ok(caregivers);
    }

    /**
     *
     * 센터의 요양보호사 배정 내역 전체 조회 API
     */
    @Override
    public ResponseEntity<List<GetServiceMatchByCenterResponse>> getAllMatchingResult(@PathVariable UUID centerId){
        List<GetServiceMatchByCenterResponse> notifications = serviceMatchQueryService.getMatchesByCenter(centerId);
        return ResponseEntity.ok(notifications);
    }

    /**
     *
     * 특정 요양 보호사의 매칭 스케줄 조회
     */
    @Override
    public ResponseEntity<List<GetCaregiverMatchesResponse>> getServiceMatchByCaregiver(@PathVariable UUID caregiverId) {
        List<GetCaregiverMatchesResponse> responses = serviceMatchQueryService.getServiceMatchByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }

    /**
     *
     * 특정 년도, 월의 요양보호사 매칭 스케줄 조회
     */
    @Override
    public ResponseEntity<List<GetCaregiverMatchesByMonth>> getMatchesByMonth(
            @RequestParam UUID centerId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer day
    ) {
        List<GetCaregiverMatchesByMonth> response = settlementQueryService.getSettlementByMonth(centerId, year, month, day);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 센터의 특정 서비스 매칭 정보 조회 API
     */
    @Override
    public ResponseEntity<GetServiceMatchByUUID> getMatchesByUUID(@PathVariable UUID serviceMatchId){
        GetServiceMatchByUUID response = serviceMatchQueryService.getMatchesByUUID(serviceMatchId);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 센터 요양보호사의 근무 상태 기반 조회 API
     */
    @Override
    public ResponseEntity<List<GetCaregiverByCaregiverStatusResponse>> getCaregiversByWorkStatus(@PathVariable UUID centerId,
                                                                                                 @RequestParam CaregiverStatus caregiverStatus) {
        List<GetCaregiverByCaregiverStatusResponse> response = caregiverQueryService.getCaregiverByWorkStatus(centerId, caregiverStatus);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 센터 요양보호사의 서비스 유형 기반 조회 API
     */
    @Override
    public ResponseEntity<List<GetCaregiverByServiceTypeResponse>> getCaregiverByServiceType(@PathVariable UUID centerId,
                                                                                             @RequestParam Set<ServiceType> serviceTypes){
        List<GetCaregiverByServiceTypeResponse> responses = caregiverQueryService.getCaregiverByServiceType(centerId, serviceTypes);
        return ResponseEntity.ok(responses);
    }

    /**
     *
     * 요양보호사의 자격증 생성 API
     */
    @Override
    public ResponseEntity<Void> createCertification(@RequestBody CreateCertificationRequest request){
        certificationCommandService.createCertification(request);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 요양보호사의 자격증 조회 API
     */
    @Override
    public ResponseEntity<GetCertificationResponse> getCertificationByCaregiver(@PathVariable UUID caregiverId){
        GetCertificationResponse response = certificationQueryService.getCertificationByCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 요양보호사 자격증 교육 상태 전환 API
     */
    @Override
    public ResponseEntity<Void> changeTrainStatus(@RequestBody UUID certificationId){
        certificationCommandService.changeTrainStatus(certificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 요양보호사 인사카드 조회
     */
    @Override
    public ResponseEntity<GetCaregiverProfileResponse> getCaregiverProfile(@RequestParam UUID caregiverId){
        GetCaregiverProfileResponse response = caregiverQueryService.getCaregiverProfile(caregiverId);
        return ResponseEntity.ok(response);
    }

    // 대시보드 조회 Api

    /**
     *
     * 센터 대시보드의 요양보호사 인구 현황 조회
     */
    @Override
    public ResponseEntity<GetDashboardPopularResponse> getDashboardPopular(@RequestParam UUID centerId){
        GetDashboardPopularResponse response = caregiverQueryService.getCaregiverStats(centerId);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 센터 대시보드의 정산 현황 조회
     */
    @Override
    public ResponseEntity<GetDashboardSettlementResponse> getDashboardSettlement(@RequestParam UUID centerId) {
        GetDashboardSettlementResponse response = settlementQueryService.getSettlementStatus(centerId);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 센터 대시보드의 근무 현황 조회
     */
    @Override
    public ResponseEntity<GetDashboardWorkStatusResponse> getDashboardWorkStatus(@RequestParam UUID centerId){
        GetDashboardWorkStatusResponse response = serviceMatchQueryService.getDashboardWorkStatus(centerId);
        return ResponseEntity.ok(response);
    }
}
