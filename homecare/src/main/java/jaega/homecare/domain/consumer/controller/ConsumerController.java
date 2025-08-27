package jaega.homecare.domain.consumer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.blacklist.dto.req.CreateBlacklistByConsumerRequest;
import jaega.homecare.domain.blacklist.dto.res.GetBlacklistByConsumerResponse;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerProfileUpdateRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerUpdateRequest;
import jaega.homecare.domain.consumer.dto.res.*;
import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.dto.res.ConsumerPendingReviewResponse;
import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.req.UpdateServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestById;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Consumer", description = "유저(consumer) API")
@RequestMapping("/api/consumer")
public interface ConsumerController {

    @Operation(summary = "수요자 회원가입 API", description = "입력받은 정보로 수요자의 회원가입을 진행합니다.")
    @ApiResponse(responseCode = "204", description = "수요자 회원가입 성공")
    @PostMapping("/register")
    ResponseEntity<Void> createConsumer(@RequestBody ConsumerSignupRequest request);

    @Operation(summary = "요양보호사 확정", description = "수요자가 배정된 요양보호사를 최종 확정합니다.")
    @ApiResponse(responseCode = "204", description = "수요자에게 요양보호자 배정 성공")
    @PostMapping("/confirm")
    ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request);

    @Operation(summary = "특정 수요자의 주간 스케줄 조회", description = "특정 수요자의 매칭된 결과를 토대로 주간 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 수요자의 주간 스케줄 조회 성공")
    @GetMapping("/schedules/{consumerId}")
    ResponseEntity<List<ConsumerScheduleResponse>> getConsumerSchedule(@PathVariable UUID consumerId);

    @Operation(summary = "특정 스케줄 상세 조회", description = "수요자가 선택한 특정 스케줄의 상세 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "스케줄 상세 조회 성공")
    @GetMapping("/schedules/{consumerId}/{id}")
    ResponseEntity<ConsumerScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id);

    @Operation(summary = "(메인 페이지) 가장 가까운 스케줄 조회", description = "수요자의 메인 페이지에서 가장 가까운 스케줄을 조회합니다..")
    @ApiResponse(responseCode = "200", description = "가까운 스케줄 조회 성공")
    @GetMapping("{id}/home/next-schedule")
    ResponseEntity<ConsumerNextScheduleResponse> getNextSchedule(@PathVariable UUID id);

    @Operation(summary = "(메인 페이지) 리뷰 요청", description = "완료된 일정 중 리뷰가 아직 등록되지 않은 일정에 대해 리뷰를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 요청 성공")
    @GetMapping("{id}/home/review-request")
    ResponseEntity<List<ReviewRequestResponse>> getReviewRequest(@PathVariable UUID id);

    @Operation(summary = "수요자의 프로필 정보 조회 API", description = "수요자의 ID로 수요자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자의 프로필 정보 조회 성공")
    @GetMapping("/{consumerId}")
    ResponseEntity<ConsumerDetailResponse> getDetail(@PathVariable UUID consumerId);

    @Operation(summary = "수요자의 프로필 정보 수정 API", description = "입력받은 정보로 수요자의 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "수요자의 프로필 정보 수정 완료")
    @PutMapping
    ResponseEntity<Void> updateProfile(@RequestParam UUID consumerId,
                                       @RequestBody ConsumerProfileUpdateRequest request);

    @Operation(summary = "블랙리스트 생성 API", description = "요양보호사를 블랙리스트에 추가합니다.")
    @ApiResponse(responseCode = "201", description = "블랙리스트 생성 성공")
    @PostMapping("/caregiver")
    ResponseEntity<UUID> createBlacklistByConsumer(@RequestBody CreateBlacklistByConsumerRequest request);

    @Operation(summary = "블랙리스트 해제 API", description = "요양보호사를 블랙리스트에서 해제합니다.")
    @ApiResponse(responseCode = "204", description = "블랙리스트 해제 성공")
    @DeleteMapping("/caregiver")
    ResponseEntity<Void> deleteBlacklistByConsumer(@RequestParam UUID caregiverBlacklistId);

    @Operation(summary = "신고자별 블랙리스트 조회 API", description = "특정 신고자가 신고한 블랙리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "블랙리스트 조회 성공")
    @GetMapping("/consumer/{consumerId}")
    ResponseEntity<List<GetBlacklistByConsumerResponse>> getBlacklistByConsumer(@PathVariable UUID consumerId);

    @Operation(summary = "리뷰 생성 API", description = "서비스 매칭에 대한 새로운 리뷰를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 생성 성공")
    @PostMapping
    ResponseEntity<UUID> createReview(@RequestBody CreateReviewRequest request) throws AccessDeniedException;

    @Operation(summary = "리뷰 조회 API", description = "서비스 매치 ID를 통해 해당 매치의 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @GetMapping("/serviceMatch/{serviceMatchId}")
    ResponseEntity<GetReviewResponse> getReviewByServiceMatch(@PathVariable UUID serviceMatchId);

    @Operation(summary = "수요자 리뷰 조회 API", description = "수요자가 작성한 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 작성한 리뷰 조회 성공")
    @GetMapping("/{consumerId}/written")
    ResponseEntity<List<ConsumerReviewResponse>> getWrittenReviews(@PathVariable UUID consumerId);

    @Operation(summary = "수요자가 작성해야할 리뷰 조회 API", description = "수요자가 아직 리뷰를 작성하지 않은 매칭들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 아직 리뷰를 작성하지 않은 매칭 일정 조회 성공")
    @GetMapping("/{consumerId}/pending")
    ResponseEntity<List<ConsumerPendingReviewResponse>> getPendingReviews(@PathVariable UUID consumerId);

    @Operation(summary = "수요자 서비스 요청 API", description = "입력받은 정보로 수요자가 서비스를 요청합니다.")
    @ApiResponse(responseCode = "204", description = "수요자가 서비스 요청 성공")
    @PostMapping
    ResponseEntity<GetCreateServiceResponse> createServiceRequest(@RequestBody ConsumerServiceRequest request);

    @Operation(summary  = "수요자가 신청한 서비스 내역 조회 API", description = "수요자가 신청한 서비스를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 조회 성공")
    @GetMapping
    ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequest(@RequestParam UUID consumerId);

    @Operation(summary  = "수요자가 신청한 서비스 내역 조회 API (신청 서비스 상태 조건)", description = "수요자가 신청한 서비스를 신청한 서비스의 상태를 조건으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 신청 상태를 조건으로 조회 성공")
    @GetMapping("/status")
    ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequestByStatus(@RequestParam UUID consumerId, ServiceRequestStatus status);


    @Operation(summary = "수요자가 신청한 서비스 내역 상세 조회", description = "수요자가 신청한 서비스 내역을 서비스 내역의 아이디로 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 상세 조회 성공")
    @GetMapping("/{serviceRequestId}")
    ResponseEntity<GetServiceRequestById> getServiceRequestById(@PathVariable UUID serviceRequestId);

    @Operation(summary = "수요자가 신청한 서비스 신청 정보 수정 API", description = "입력받은 정보로 서비스 신청 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "서비스 신청 정보 수정 성공")
    @PutMapping("/request")
    ResponseEntity<Void> updateServiceRequest(@RequestParam UUID serviceRequestId,
                                              @RequestBody UpdateServiceRequest request);
}
