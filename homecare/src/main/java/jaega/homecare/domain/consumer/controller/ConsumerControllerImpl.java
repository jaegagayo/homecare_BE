package jaega.homecare.domain.consumer.controller;

import jaega.homecare.domain.blacklist.dto.req.CreateBlacklistByConsumerRequest;
import jaega.homecare.domain.blacklist.dto.res.GetBlacklistByConsumerResponse;
import jaega.homecare.domain.blacklist.service.command.BlacklistCommandService;
import jaega.homecare.domain.blacklist.service.query.BlacklistQueryService;
import jaega.homecare.domain.consumer.dto.req.ConsumerProfileUpdateRequest;
import jaega.homecare.domain.consumer.dto.res.*;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecommendRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.service.command.RecurringOfferCommandService;
import jaega.homecare.domain.recurringOffer.service.query.RecurringOfferQueryService;
import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.serviceMatch.dto.res.*;
import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import jaega.homecare.domain.review.service.command.ReviewCommandService;
import jaega.homecare.domain.review.service.query.ReviewQueryService;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.req.UpdateServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestById;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.serviceRequest.service.command.ServiceRequestCommandService;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jaega.homecare.domain.consumer.service.command.ConsumerCommandService;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.voucher.service.query.VoucherQueryService;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageResponse;
import jaega.homecare.domain.voucherUsage.service.query.VoucherUsageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer")
public class ConsumerControllerImpl implements ConsumerController {

    private final ConsumerCommandService consumerCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final ServiceRequestQueryService serviceRequestQueryService;
    private final ServiceRequestCommandService serviceRequestCommandService;
    private final ConsumerQueryService consumerQueryService;
    private final BlacklistCommandService blacklistCommandService;
    private final BlacklistQueryService blacklistQueryService;
    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final RecurringOfferCommandService recurringOfferCommandService;
    private final RecurringOfferQueryService recurringOfferQueryService;
    private final VoucherQueryService voucherQueryService;
    private final VoucherUsageQueryService voucherUsageQueryService;

    // 수요자 회원가입 API
    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody ConsumerSignupRequest request) {
        consumerCommandService.signupConsumer(request);
        return ResponseEntity.noContent().build();
    }

    // 수요자 로그인 API
    @Override
    public ResponseEntity<ConsumerLoginResponse> loginConsumer(@RequestBody UserLoginRequest request){
        ConsumerLoginResponse response = consumerCommandService.loginConsumer(request);
        return ResponseEntity.ok(response);
    }

    // (마이페이지) 수요자의 프로필 정보 조회 API
    @Override
    public ResponseEntity<ConsumerDetailResponse> getDetail(@RequestParam UUID consumerId){
        ConsumerDetailResponse response = consumerQueryService.getDetail(consumerId);
        return ResponseEntity.ok(response);
    }

    // (마이페이지) 수요자의 프로필 정보 수정
    @Override
    public ResponseEntity<Void> updateProfile(@RequestParam UUID consumerId,
                                              @RequestBody ConsumerProfileUpdateRequest request){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        consumerCommandService.updateConsumer(request, consumer);
        return ResponseEntity.noContent().build();
    }

    // 요양보호사 확정
    // TODO : 추후, 요양보호사 매칭 확정 API가 구현되면 제거될 수 있습니다. ( 이전 기획의 매칭 API )
    @Override
    public ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request){

        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(request.serviceRequestId());

        serviceMatchCommandService.createServiceMatch(
                new CreateServiceMatchRequest(
                        request.serviceRequestId(),
                        request.caregiverId(),
                        serviceRequest.getPreferredStartTime(),
                        serviceRequest.getPreferredEndTime(),
                        serviceRequest.getRequestDate()
                )
        );
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 일정 조회 API
     */

    // 특정 수요자의 주간 스케줄 조회
    @Override
    public ResponseEntity<List<ConsumerScheduleResponse>> getConsumerSchedule(@RequestParam UUID consumerId){
        LocalDate today = LocalDate.now();
        List<ConsumerScheduleResponse> responses = serviceMatchQueryService.getConsumerSchedule(consumerId, today);
        return ResponseEntity.ok(responses);
    }

    // 특정 스케줄 상세 조회
    @Override
    public ResponseEntity<ConsumerScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id){
        ConsumerScheduleDetailResponse response = serviceMatchQueryService.getConsumerScheduleDetail(id);
        return ResponseEntity.ok(response);
    }

    // (메인 페이지) 가장 가까운 일정 조회
    @Override
    public ResponseEntity<ConsumerNextScheduleResponse> getNextSchedule(@RequestParam UUID consumerId){
        ConsumerNextScheduleResponse response = serviceMatchQueryService.getConsumerNextSchedule(consumerId);
        return ResponseEntity.ok(response);
    }

    // (메인 페이지) 리뷰를 작성하지 않은 일정 조회
    @Override
    public ResponseEntity<List<GetScheduleWithoutReviewResponse>> getScheduleWithoutReview(@RequestParam UUID consumerId){
        List<GetScheduleWithoutReviewResponse> responses = serviceMatchQueryService.getScheduleWithoutReview(consumerId);
        return ResponseEntity.ok(responses);
    }

    // (마이 페이지) 리뷰를 작성해야 할 일정 조회
    @Override
    public ResponseEntity<List<GetScheduleWithoutReviewResponse>> getPendingReviews(@PathVariable UUID consumerId) {
        List<GetScheduleWithoutReviewResponse> pending = serviceMatchQueryService.getScheduleWithoutReview(consumerId);
        return ResponseEntity.ok(pending);
    }

    // (메인 페이지) 수요자에게 거절된 일정 조회
    @Override
    public ResponseEntity<List<ConsumerCancelledScheduleResponse>> getCancelledSchedule(@RequestParam UUID consumerId){
        List<ConsumerCancelledScheduleResponse> responses = serviceMatchQueryService.getCancelledSchedules(consumerId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 서비스 요청 API
     */

    // 수요자 서비스 신청 API
    @Override
    public ResponseEntity<GetCreateServiceResponse> createServiceRequest(@RequestBody ConsumerServiceRequest request){
        GetCreateServiceResponse response = serviceRequestCommandService.createServiceRequest(request);
        return ResponseEntity.ok(response);
    }

    // 수요자가 신청한 서비스 신청 정보 수정 API
    @Override
    public ResponseEntity<Void> updateServiceRequest(@RequestParam UUID serviceRequestId,
                                                     @RequestBody UpdateServiceRequest request){
        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(serviceRequestId);
        serviceRequestCommandService.updateServiceRequest(request, serviceRequest);
        return ResponseEntity.noContent().build();
    }

    // 수요자가 신청한 서비스 신청 정보 조회 API
    @Override
    public ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequest(@RequestParam UUID consumerId){
        List<GetServiceRequestResponse> response = serviceRequestQueryService.findConsumerRequests(consumerId);
        return ResponseEntity.ok(response);
    }

    // 수요자가 신청한 서비스 정보 리스트 조회 API (신청 서비스 상태 조건)
    @Override
    public ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequestByStatus(@RequestParam UUID consumerId, ServiceRequestStatus status){
        List<GetServiceRequestResponse> response = serviceRequestQueryService.findConsumerRequestsByStatus(consumerId, status);
        return ResponseEntity.ok(response);
    }

    // 수요자가 신청한 서비스 정보 상세 상세 조회
    @Override
    public ResponseEntity<GetServiceRequestById> getServiceRequestById(@RequestParam UUID serviceRequestId) {
        GetServiceRequestById response = serviceRequestQueryService.findServiceRequestById(serviceRequestId);
        return ResponseEntity.ok(response);
    }


    // (메인 페이지) 거절된 수요자 매칭의 서비스 신청 취소
    @Override
    public ResponseEntity<Void> rejectServiceRequestByMatch(@RequestParam UUID serviceMatchId){
        serviceRequestCommandService.rejectServiceRequestByMatch(serviceMatchId);
        return ResponseEntity.noContent().build();
    }


    /**
     *
     * 정기 제안 관련  API
     */

    // 정기 제안 신청 작성 API
    @Override
    public ResponseEntity<Void> createRecurringOffer(@RequestBody CreateRecurringOfferRequest request){
        recurringOfferCommandService.createRecurringOffer(request);

        return ResponseEntity.noContent().build();
    }

    // (마이페이지) 정기 제안 신청 조회 API
    @Override
    public ResponseEntity<List<GetRecurringOfferResponse>> getRecurringOfferByConsumer(@RequestParam UUID consumerId){
        List<GetRecurringOfferResponse> responses = recurringOfferQueryService.findRecurringOfferByConsumer(consumerId);
        return ResponseEntity.ok(responses);
    }

    // 정기 제안 상세 조회 API
    @Override
    public ResponseEntity<GetRecurringOfferDetailResponse> getRecurringOfferDetail(@PathVariable UUID recurringOfferId){
        GetRecurringOfferDetailResponse response = recurringOfferQueryService.findRecurringOfferDetail(recurringOfferId);
        return ResponseEntity.ok(response);
    }

    // (메인 페이지) 정기 제안 신청 후 상태 변화 시 알림 조회
    @Override
    public ResponseEntity<List<GetUnreadRecurringOfferResponse>> getUnreadRecurringOffersForConsumer(@RequestParam UUID consumerId) {
        List<GetUnreadRecurringOfferResponse> responses = recurringOfferQueryService.findByUnreadRecurringOffers(consumerId);
        return ResponseEntity.ok(responses);
    }

    // (메인 페이지) 수요자의 정기 제안 추천 알림 조회
    @Override
    public ResponseEntity<List<GetRecommendRecurringOfferResponse>> getRecommendRecurringOffersForConsumer(@RequestParam UUID consumerId){
        List<GetRecommendRecurringOfferResponse> recurringOfferResponses = recurringOfferQueryService.findRecommendedRecurringOffers(consumerId);
        return ResponseEntity.ok(recurringOfferResponses);
    }


    /**
     * 바우처 관련 API
     */

    // 바우처 사용 안내 API
    @Override
    public ResponseEntity<VoucherUsageGuideResponse> getVoucherUsageGuide(@PathVariable UUID consumerId){
        UUID voucherId = voucherQueryService.getVoucherIdByConsumerId(consumerId);
        Long totalVoucherAmount = voucherQueryService.getTotalAmount(voucherId);

        VoucherUsageGuideResponse response = voucherUsageQueryService.getVoucherUsageGuide(voucherId, totalVoucherAmount);

        return ResponseEntity.ok(response);
    }

    // (마이페이지) 재가 급여(바우처) 내역 조회
    @Override
    public ResponseEntity<VoucherUsageResponse> getVoucherByConsumer(
            @RequestParam UUID consumerId,
            @RequestParam int year,
            @RequestParam int month
    ){
        VoucherUsageResponse response = voucherUsageQueryService.getVoucherUsageSummary(consumerId, year, month);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 블랙리스트 관련 API
     */

    // 블랙리스트 생성 API
    @Override
    public ResponseEntity<UUID> createBlacklistByConsumer(@RequestBody CreateBlacklistByConsumerRequest request) {
        UUID blacklistId = blacklistCommandService.createBlacklistByConsumer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(blacklistId);
    }

    // 블랙리스트 해제 API
    @Override
    public ResponseEntity<Void> deleteBlacklistByConsumer(@RequestParam UUID caregiverBlacklistId){
        blacklistCommandService.deleteBlacklistByConsumer(caregiverBlacklistId);
        return ResponseEntity.noContent().build();
    }

    // (마이페이지) 블랙리스트 조회 API
    @Override
    public ResponseEntity<List<GetBlacklistByConsumerResponse>> getBlacklistByConsumer(@RequestParam UUID consumerId) {
        List<GetBlacklistByConsumerResponse> responses = blacklistQueryService.getBlacklistByConsumer(consumerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 리뷰 관련 API
     */

    // 리뷰 등록 API
    @Override
    public ResponseEntity<UUID> createReview(@RequestBody CreateReviewRequest request) throws AccessDeniedException {
        UUID reviewId = reviewCommandService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    // 매칭 일정의 리뷰 조회 API
    @Override
    public ResponseEntity<GetReviewResponse> getReviewByServiceMatch(@RequestParam UUID serviceMatchId) {
        GetReviewResponse response = reviewQueryService.getReviewByServiceMatch(serviceMatchId);
        return ResponseEntity.ok(response);
    }

    // (마이페이지) 수요자 리뷰 조회 API
    @Override
    public ResponseEntity<List<ConsumerReviewResponse>> getWrittenReviews(@RequestParam UUID consumerId) {
        List<ConsumerReviewResponse> reviews = reviewQueryService.getWrittenReviews(consumerId);
        return ResponseEntity.ok(reviews);
    }

}
