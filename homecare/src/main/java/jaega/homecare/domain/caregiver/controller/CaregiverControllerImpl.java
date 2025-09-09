package jaega.homecare.domain.caregiver.controller;

import jaega.homecare.domain.caregiver.dto.req.CaregiverSignupRequest;
import jaega.homecare.domain.caregiver.dto.req.ChoiceCaregiverCenterRequest;
import jaega.homecare.domain.caregiver.dto.res.*;
import jaega.homecare.domain.caregiver.service.command.CaregiverCommandService;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.service.query.CaregiverCenterQueryService;
import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.req.UpdateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.res.GetCaregiverPreferenceResponse;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.service.command.CaregiverPreferenceCommandService;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import jaega.homecare.domain.recurringOffer.dto.res.GetCaregiverRecurringOfferSummaryResponse;
import jaega.homecare.domain.recurringOffer.service.command.RecurringOfferCommandService;
import jaega.homecare.domain.recurringOffer.service.query.RecurringOfferQueryService;
import jaega.homecare.domain.review.dto.res.CaregiverReviewItem;
import jaega.homecare.domain.serviceMatch.dto.res.CaregiverScheduleDetailResponse;
import jaega.homecare.domain.serviceMatch.dto.res.CaregiverScheduleResponse;
import jaega.homecare.domain.review.dto.res.CaregiverReviewDetailResponse;
import jaega.homecare.domain.review.dto.res.CaregiverReviewSummaryResponse;
import jaega.homecare.domain.review.service.query.ReviewQueryService;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.dto.res.GetCaregiverCenterSettlementResponse;
import jaega.homecare.domain.settlement.dto.res.GetSettlementByCaregiverResponse;
import jaega.homecare.domain.settlement.dto.res.GetSettlementResponse;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/caregiver")
public class CaregiverControllerImpl implements CaregiverController {

    private final SettlementCommandService settlementCommandService;
    private final SettlementQueryService settlementQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final CaregiverCenterQueryService caregiverCenterQueryService;
    private final CaregiverCommandService caregiverCommandService;
    private final CaregiverQueryService caregiverQueryService;
    private final ReviewQueryService reviewQueryService;
    private final CaregiverPreferenceCommandService caregiverPreferenceCommandService;
    private final CaregiverPreferenceQueryService caregiverPreferenceQueryService;
    private final RecurringOfferQueryService recurringOfferQueryService;
    private final RecurringOfferCommandService recurringOfferCommandService;

    // 요양보호사 회원가입
    @Override
    public ResponseEntity<GetCaregiverSignupResponse> signupCaregiver(@RequestBody CaregiverSignupRequest request){
        GetCaregiverSignupResponse response = caregiverCommandService.signupCaregiver(request);
        return ResponseEntity.ok(response);
    }

    // 요양보호사 로그인
    @Override
    public ResponseEntity<CaregiverLoginResponse> loginCaregiver(@RequestBody UserLoginRequest request){
        CaregiverLoginResponse response = caregiverCommandService.loginCaregiver(request);
        return ResponseEntity.ok(response);
    }

    // 요양 보호사 승인 상태 조회
    // TODO : 인증 기능 구현 시 다른 페이지로 이동 못하도록 CORS 도입 고려
    @Override
    public ResponseEntity<GetCaregiverVerifiedStatusResponse> getCaregiverVerifiedStatus(@RequestParam UUID caregiverId){
        GetCaregiverVerifiedStatusResponse response = caregiverQueryService.getCaregiverVerifiedStatus(caregiverId);
        return ResponseEntity.ok(response);
    }

    // 요양보호사 프로필 정보 조회
    @Override
    public ResponseEntity<GetCaregiverProfileResponse> getCaregiverProfile(@RequestParam UUID caregiverId){
        GetCaregiverProfileResponse response = caregiverQueryService.getCaregiverProfileByCaregiver(caregiverId);
        return ResponseEntity.ok(response);

    }

    // (마이페이지) 요양보호사 리뷰 조회 API
    @Override
    public ResponseEntity<List<CaregiverReviewItem>> getReviewByCaregiver(UUID caregiverId){
        List<CaregiverReviewItem> response = reviewQueryService.getReviewByCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetCaregiverCenterSettlementResponse>> getSettlementByCaregiver(UUID caregiverId){
        List<GetCaregiverCenterSettlementResponse> responses = settlementQueryService.getSettlementHistoryByCaregiver(caregiverId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 요양보호사 근무조건 API
     */

    @Override
    public ResponseEntity<Void> createCaregiverPreference(
            @RequestParam UUID caregiverId,
            @RequestBody CreateCaregiverPreferenceRequest request){
        caregiverPreferenceCommandService.createCaregiverPreference(request, caregiverId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateCaregiverPreference(
            @RequestParam UUID caregiverId,
            @RequestBody UpdateCaregiverPreferenceRequest request){
        CaregiverPreference caregiverPreference = caregiverPreferenceQueryService.findCaregiverPreferenceByCaregiver(caregiverId);
        caregiverPreferenceCommandService.updateCaregiverPreference(caregiverPreference, request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GetCaregiverPreferenceResponse> getCaregiverPreferenceByCaregiver(@RequestParam UUID caregiverId){
        GetCaregiverPreferenceResponse response = caregiverPreferenceQueryService.getCaregiverPreferenceByCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

    /**
     * 센터 관련 API
     */

    // 소속 중인 센터 목록 조회
    @Override
    public ResponseEntity<List<SelectableCaregiverCenter>> getMyActiveCenters(@RequestParam UUID caregiverId) {
        List<CaregiverCenter> centers = caregiverCenterQueryService.getActiveCaregiverCenters(caregiverId);

        List<SelectableCaregiverCenter> response = centers.stream()
                .map(c -> new SelectableCaregiverCenter(
                        c.getCaregiverCenterId(),
                        c.getCenter().getName(),
                        c.getCenter().getUser().getPhone()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // 요양보호사의 명시적 센터 선택
    @Override
    public ResponseEntity<Void> chooseCaregiverCenter(@RequestBody ChoiceCaregiverCenterRequest request) {
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(request.serviceMatchId());


        settlementCommandService.createSettlement(
                new CreateSettlementRequest(
                        request.caregiverCenterId(),
                        serviceMatch.getServiceMatchId(),
                        request.distanceLog()
                )
        );

        return ResponseEntity.noContent().build();
    }

    /**
     * 일정 조회 API
     */

    // 특정 요양 보호사의 주간 일정 조회
    @Override
    public ResponseEntity<List<CaregiverScheduleResponse>> getCaregiverSchedule(@RequestParam UUID caregiverId){
        LocalDate today = LocalDate.now();
        List<CaregiverScheduleResponse> responses = serviceMatchQueryService.getCaregiverSchedule(caregiverId, today);
        return ResponseEntity.ok(responses);
    }

    // 특정 스케줄 상세 조회
    @Override
    public ResponseEntity<CaregiverScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id){
        CaregiverScheduleDetailResponse response = serviceMatchQueryService.getCaregiverScheduleDetail(id);
        return ResponseEntity.ok(response);
    }

    // (메인 페이지) 요양 보호사의 당일 스케줄 조회
    @Override
    public ResponseEntity<List<CaregiverScheduleResponse>> getTodaySchedule(@RequestParam UUID caregiverId){
        LocalDate today = LocalDate.now();
        List<CaregiverScheduleResponse> responses = serviceMatchQueryService.getCaregiverScheduleByDate(caregiverId, today);
        return ResponseEntity.ok(responses);
    }

    // (메인 페이지) 요양 보호사의 내일 예정 스케줄 조회
    @Override
    public ResponseEntity<List<CaregiverScheduleResponse>> getTomorrowSchedule(@RequestParam UUID caregiverId){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<CaregiverScheduleResponse> responses = serviceMatchQueryService.getCaregiverScheduleByDate(caregiverId, tomorrow);
        return ResponseEntity.ok(responses);
    }

    // 요양보호사에게 확정된 단발 일정을 거절
    @Override
    public ResponseEntity<Void> rejectScheduleReject(UUID serviceMatchId) {
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(serviceMatchId);
        serviceMatchCommandService.rejectMatchStatus(serviceMatch);

        return ResponseEntity.noContent().build();
    }


    /**
     *  리뷰 조회 API
     *
     */

    @Override
    public ResponseEntity<CaregiverReviewSummaryResponse> getReviews(@RequestParam UUID caregiverId) {
        CaregiverReviewSummaryResponse response = reviewQueryService.getReviewsForCaregiver(caregiverId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CaregiverReviewDetailResponse> getReviewDetail(@PathVariable UUID reviewId) {
        CaregiverReviewDetailResponse response = reviewQueryService.getReviewDetail(reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * 정기 제안 관련 API
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
