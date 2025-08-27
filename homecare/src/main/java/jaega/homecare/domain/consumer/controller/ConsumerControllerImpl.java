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
import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.dto.res.ConsumerPendingReviewResponse;
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

    /**
     *
     * 수요자 회원가입 API
     */
    @Override
    public ResponseEntity<Void> createConsumer(@RequestBody ConsumerSignupRequest request) {
        consumerCommandService.signupConsumer(request);
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * 요양보호사 확정
     */
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
     * 특정 수요자의 주간 스케줄 조회
     */
    @Override
    public ResponseEntity<List<ConsumerScheduleResponse>> getConsumerSchedule(@PathVariable UUID consumerId){
        LocalDate today = LocalDate.now();
        List<ConsumerScheduleResponse> responses = consumerQueryService.getConsumerSchedule(consumerId, today);
        return ResponseEntity.ok(responses);
    }

    /**
     *
     * 특정 스케줄 상세 조회
     */
    @Override
    public ResponseEntity<ConsumerScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id){
        ConsumerScheduleDetailResponse response = consumerQueryService.getScheduleDetail(id);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * (메인 페이지) 가장 가까운 일정 조회
     */
    @Override
    public ResponseEntity<ConsumerNextScheduleResponse> getNextSchedule(@PathVariable UUID id){
        ConsumerNextScheduleResponse response = consumerQueryService.getNextSchedule(id);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * (메인 페이지) 리뷰 요청
     */
    @Override
    public ResponseEntity<List<ReviewRequestResponse>> getReviewRequest(@PathVariable UUID id){
        List<ReviewRequestResponse> responses = consumerQueryService.getReviewRequest(id);
        return ResponseEntity.ok(responses);
    }

    /**
     * (내 정보) 프로필 정보 조회
     */

    @Override
    public ResponseEntity<ConsumerDetailResponse> getDetail(@PathVariable UUID consumerId){
        ConsumerDetailResponse response = consumerQueryService.getDetail(consumerId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> updateProfile(@RequestParam UUID consumerId,
                                              @RequestBody ConsumerProfileUpdateRequest request){
        Consumer consumer = consumerQueryService.getConsumer(consumerId);
        consumerCommandService.updateConsumer(request, consumer);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<Void> deleteBlacklistByConsumer(@PathVariable UUID caregiverBlacklistId){
        blacklistCommandService.deleteBlacklistByConsumer(caregiverBlacklistId);
        return ResponseEntity.noContent().build();
    }

    // 신고자별 블랙리스트 조회 API
    @Override
    public ResponseEntity<List<GetBlacklistByConsumerResponse>> getBlacklistByConsumer(@PathVariable UUID consumerId) {
        List<GetBlacklistByConsumerResponse> responses = blacklistQueryService.getBlacklistByConsumer(consumerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 리뷰 관련 API
     */

    // 리뷰 생성 API
    // TODO : JWT 인증 로직 구현 시 직접 consumerId에서 가져오는 게 아닌 인증값에서 가져오도록 할 것 !
    // TODO : 추가로, Exception 또한 JWT에서 하기 !!
    @Override
    public ResponseEntity<UUID> createReview(@RequestBody CreateReviewRequest request) throws AccessDeniedException {
        UUID reviewId = reviewCommandService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    // 리뷰 조회 API
    @Override
    public ResponseEntity<GetReviewResponse> getReviewByServiceMatch(@PathVariable UUID serviceMatchId) {
        GetReviewResponse response = reviewQueryService.getReviewByServiceMatch(serviceMatchId);
        return ResponseEntity.ok(response);
    }

    //
    @Override
    public ResponseEntity<List<ConsumerReviewResponse>> getWrittenReviews(@PathVariable UUID consumerId) {
        List<ConsumerReviewResponse> reviews = reviewQueryService.getWrittenReviews(consumerId);
        return ResponseEntity.ok(reviews);
    }

    @Override
    public ResponseEntity<List<ConsumerPendingReviewResponse>> getPendingReviews(@PathVariable UUID consumerId) {
        List<ConsumerPendingReviewResponse> pending = serviceMatchQueryService.getPendingReviews(consumerId);
        return ResponseEntity.ok(pending);
    }

    /**
     * 서비스 요청 API
     */

    // 수요자 서비스 요청 API
    @Override
    public ResponseEntity<GetCreateServiceResponse> createServiceRequest(@RequestBody ConsumerServiceRequest request){
        GetCreateServiceResponse response = serviceRequestCommandService.createServiceRequest(request);
        return ResponseEntity.ok(response);
    }

    // 수요자가 신청한 서비스 내역 조회 API
    @Override
    public ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequest(@RequestParam UUID consumerId){
        List<GetServiceRequestResponse> response = serviceRequestQueryService.findConsumerRequests(consumerId);
        return ResponseEntity.ok(response);
    }

    // 수요자가 신청한 서비스 내역 조회 API (신청 서비스 상태 조건)
    @Override
    public ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequestByStatus(@RequestParam UUID consumerId, ServiceRequestStatus status){
        List<GetServiceRequestResponse> response = serviceRequestQueryService.findConsumerRequestsByStatus(consumerId, status);
        return ResponseEntity.ok(response);
    }

    // 수요자가 신청한 서비스 내역 상세 조회
    @Override
    public ResponseEntity<GetServiceRequestById> getServiceRequestById(UUID serviceRequestId) {
        GetServiceRequestById response = serviceRequestQueryService.findServiceRequestById(serviceRequestId);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Void> updateServiceRequest(@RequestParam UUID serviceRequestId,
                                                     @RequestBody UpdateServiceRequest request){
        ServiceRequest serviceRequest = serviceRequestQueryService.getServiceRequest(serviceRequestId);
        serviceRequestCommandService.updateServiceRequest(request, serviceRequest);
        return ResponseEntity.noContent().build();
    }
}
