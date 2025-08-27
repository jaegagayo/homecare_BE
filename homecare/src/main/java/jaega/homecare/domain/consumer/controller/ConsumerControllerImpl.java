package jaega.homecare.domain.consumer.controller;

import jaega.homecare.domain.consumer.dto.req.ConsumerProfileUpdateRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerUpdateRequest;
import jaega.homecare.domain.consumer.dto.res.*;
import jaega.homecare.domain.consumer.entity.Consumer;
import jaega.homecare.domain.consumer.service.query.ConsumerQueryService;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.service.query.ServiceRequestQueryService;
import jaega.homecare.domain.consumer.service.command.ConsumerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/consumer")
public class ConsumerControllerImpl implements ConsumerController {

    private final ConsumerCommandService consumerCommandService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final ServiceRequestQueryService serviceRequestQueryService;
    private final ConsumerQueryService consumerQueryService;

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

}
