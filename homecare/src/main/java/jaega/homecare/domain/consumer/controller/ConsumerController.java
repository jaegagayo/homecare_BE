package jaega.homecare.domain.consumer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.blacklist.dto.req.CreateBlacklistByConsumerRequest;
import jaega.homecare.domain.blacklist.dto.res.GetBlacklistByConsumerResponse;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerProfileUpdateRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.consumer.dto.res.*;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecommendRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.serviceMatch.dto.res.ConsumerNextScheduleResponse;
import jaega.homecare.domain.serviceMatch.dto.res.ConsumerScheduleDetailResponse;
import jaega.homecare.domain.serviceMatch.dto.res.ConsumerScheduleResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetScheduleWithoutReviewResponse;
import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.req.UpdateServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.GetCreateServiceResponse;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestById;
import jaega.homecare.domain.serviceRequest.dto.res.GetServiceRequestResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageResponse;
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

    @Operation(summary = "수요자 로그인 API", description = "입력받은 정보로 수요자의 로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "수요자 로그인 성공")
    @PostMapping("/login")
    ResponseEntity<ConsumerLoginResponse> loginConsumer(@RequestBody UserLoginRequest request);

    @Operation(summary = "(마이페이지) 수요자의 프로필 정보 조회 API", description = "수요자의 ID로 수요자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자의 프로필 정보 조회 성공")
    @GetMapping("/my-page")
    ResponseEntity<ConsumerDetailResponse> getDetail(@RequestParam UUID consumerId);

    @Operation(summary = "(마이페이지) 수요자의 프로필 정보 수정 API", description = "입력받은 정보로 수요자의 프로필 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "수요자의 프로필 정보 수정 완료")
    @PutMapping("/my-page")
    ResponseEntity<Void> updateProfile(@RequestParam UUID consumerId,
                                       @RequestBody ConsumerProfileUpdateRequest request);

    // TODO : 추후, 요양보호사 매칭 확정 API가 구현되면 제거될 수 있습니다. ( 이전 기획의 매칭 API )
    @Operation(summary = "요양보호사 확정", description = "수요자가 배정된 요양보호사를 최종 확정합니다.")
    @ApiResponse(responseCode = "204", description = "수요자에게 요양보호자 배정 성공")
    @PostMapping("/request/matching/confirm")
    ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request);

    /**
     *
     * 스케줄 조회 API
     */

    @Operation(summary = "특정 수요자의 주간 스케줄 조회", description = "특정 수요자의 매칭된 결과를 토대로 주간 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 수요자의 주간 스케줄 조회 성공")
    @GetMapping("/schedule")
    ResponseEntity<List<ConsumerScheduleResponse>> getConsumerSchedule(@RequestParam UUID consumerId);

    @Operation(summary = "특정 스케줄 상세 조회", description = "수요자가 선택한 특정 스케줄의 상세 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "스케줄 상세 조회 성공")
    @GetMapping("/schedule/{id}")
    ResponseEntity<ConsumerScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id);

    @Operation(summary = "(메인 페이지) 가장 가까운 일정 조회", description = "수요자의 메인 페이지에서 가장 가까운 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "가까운 스케줄 조회 성공")
    @GetMapping("/home/next-schedule")
    ResponseEntity<ConsumerNextScheduleResponse> getNextSchedule(@RequestParam UUID consumerId);

    @Operation(summary = "(메인 페이지) 리뷰를 작성하지 않은 일정 조회", description = "완료된 일정 중 리뷰가 아직 등록되지 않은 일정에 대해 리뷰를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 요청 성공")
    @GetMapping("/home/notification/review")
    ResponseEntity<List<GetScheduleWithoutReviewResponse>> getScheduleWithoutReview(@RequestParam UUID consumerId);


    /**
     *
     * 서비스 요청 API
     */

    @Operation(summary = "수요자 서비스 신청 API", description = "입력받은 정보로 수요자가 서비스를 신청합니다.")
    @ApiResponse(responseCode = "204", description = "수요자가 서비스 요청 성공")
    @PostMapping("/request")
    ResponseEntity<GetCreateServiceResponse> createServiceRequest(@RequestBody ConsumerServiceRequest request);

    @Operation(summary = "수요자가 신청한 서비스 신청 정보 수정 API", description = "입력받은 정보로 수요자가 서비스 신청 정보를 수정합니다.")
    @ApiResponse(responseCode = "204", description = "서비스 신청 정보 수정 성공")
    @PutMapping("/request")
    ResponseEntity<Void> updateServiceRequest(@RequestParam UUID serviceRequestId,
                                              @RequestBody UpdateServiceRequest request);

    @Operation(summary  = "수요자가 신청한 서비스 정보 조회 API", description = "수요자가 신청한 서비스를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 조회 성공")
    @GetMapping("/request")
    ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequest(@RequestParam UUID consumerId);

    @Operation(summary  = "수요자가 신청한 서비스 정보 리스트 조회 API (신청 서비스 상태 조건)", description = "수요자가 신청한 서비스를 신청한 서비스의 상태를 조건으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 신청 상태를 조건으로 조회 성공")
    @GetMapping("/request-status")
    ResponseEntity<List<GetServiceRequestResponse>> getConsumerServiceRequestByStatus(@RequestParam UUID consumerId, ServiceRequestStatus status);


    @Operation(summary = "수요자가 신청한 서비스 정보 상세 조회", description = "수요자가 신청한 서비스 내역을 서비스 내역의 아이디로 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 상세 조회 성공")
    @GetMapping("/{requestId}")
    ResponseEntity<GetServiceRequestById> getServiceRequestById(@RequestParam UUID requestId);


    /**
     *
     * 정기 제안 관련 API
     */


    @Operation(summary = "정기 제안 신청 작성 API", description = "입력받은 정보로 수요자와 요양보호사 간의 정기 제안 신청을 생성합니다.<br>" +
            "정기 신청의 1회 서비스 소요 시간을 시간/분 단위로 입력받으며 다음과 같이 duration 입력하시면 됩니다." +
            "ex) 3h30m : 3시간 30분, 3h : 3시간")
    @ApiResponse(responseCode = "204", description = "정기 제안 신청 작성 성공")
    @PostMapping("/recurring")
    ResponseEntity<Void> createRecurringOffer(@RequestBody CreateRecurringOfferRequest request);

    @Operation(summary = "(마이페이지) 정기 제안 신청 조회 API", description = "수요자가 작성한 정기 제안 신청서을 조회합니다." +
            "정기 신청의 1회 서비스 소요 시간이 다음과 같은 형식으로 조회됩니다." +
            "ex) 3h30m : 3시간 30분, 3h : 3시간")
    @ApiResponse(responseCode = "200", description = "수요자의 정기 제안 신청 조회 성공")
    @GetMapping("/my-page/recurring")
    ResponseEntity<List<GetRecurringOfferResponse>> getRecurringOfferByConsumer(@RequestParam UUID consumerId);

    @Operation(summary = "정기 제안 상세 조회 API", description = "작성된 정기 제안 신청서를 상세 조회합니다.<br>" +
            "정기 신청의 1회 서비스 소요 시간이 다음과 같은 형식으로 조회됩니다." +
            "ex) 3h30m : 3시간 30분, 3h : 3시간")
    @ApiResponse(responseCode = "200", description = "정기 제안 신청 상세 조회 성공")
    @GetMapping("/my-page/recurring/{recurringId}")
    ResponseEntity<GetRecurringOfferDetailResponse> getRecurringOfferDetail(@PathVariable UUID recurringId);

    @Operation(summary = "(메인 페이지) 정기 제안 신청 후 상태 변화 시 알림 조회", description = "메인 페이지에서 수요자가 '읽지 않은(아직 해당 정기 제안의 상세 정보를 조회하지 않은)<br>" +
            " 정기 제안 상태 기반 알림을 조회합니다.<br>" +
            "1. 승인 대기(PENDING)<br>" +
            "2. 승인 완료(APPROVED)<br>" +
            "3. 거절(REJECTED)<br>" +
            "RequestParam인 이유는 추후 JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함입니다.")
    @ApiResponse(responseCode = "200", description = "수요자에게 정기 제안 상태 기반 알림 조회 성공")
    @GetMapping("/home/notification/recurring")
    ResponseEntity<List<GetUnreadRecurringOfferResponse>> getUnreadRecurringOffersForConsumer(@RequestParam UUID consumerId);

    @Operation(summary = "(메인 페이지) 수요자의 정기 제안 추천 알림 조회", description = "메인 페이지에서 수요자에게 다음과 같은 조건에 맞는 정기 제안 알림을 조회합니다.<br>" +
            "1. 일정 리뷰 평점이 4점 이상<br>" +
            "2. 아직 정기 제안을 신청하지 않았음<br>" +
            "3. 매칭이 종료된 일정<br>" +
            "RequestParam인 이유는 추후 JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함입니다.")
    @ApiResponse(responseCode = "200", description = "수요자에게 조건에 맞는 정기 제안 추천 알림 조회 성공")
    @GetMapping("/home/recommend/recurring")
    ResponseEntity<List<GetRecommendRecurringOfferResponse>> getRecommendRecurringOffersForConsumer(@RequestParam UUID consumerId);

    /**
     *
     * 바우처 관련 API
     */

    @Operation(summary = "바우처 사용 안내 API", description = "재가 요양 서비스 신청 시 바우처 사용 가능 여부와 예상 본인 부담금을 안내합니다." +
            "단, 본인 부담률 15%를 초과한 경우에만 안내하도록 합니다.")
    @ApiResponse(responseCode = "200", description = "바우처 사용 안내 조회 성공")
    @GetMapping("/request/voucher")
    ResponseEntity<VoucherUsageGuideResponse> getVoucherUsageGuide(@RequestParam UUID consumerId);

    // (마이페이지) 재가 급여(바우처) 내역 조회
    @Operation(summary = "(마이페이지) 재가 급여(바우처) 내역 조회", description = "마이페이지에서 수요자의 재가 급여(바우처) 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "(마이페이지) 사용자의 재가 급여 내역 조회 성공")
    @GetMapping("/my-page/voucher")
    ResponseEntity<VoucherUsageResponse> getVoucherByConsumer(
            @RequestParam UUID consumerId,
            @RequestParam int year,
            @RequestParam int month
    );

    /**
     *
     * 블랙리스트 관련 API
     */

    @Operation(summary = "블랙리스트 생성 API", description = "요양보호사를 블랙리스트에 추가합니다.")
    @ApiResponse(responseCode = "201", description = "블랙리스트 생성 성공")
    @PostMapping("/blacklist")
    ResponseEntity<UUID> createBlacklistByConsumer(@RequestBody CreateBlacklistByConsumerRequest request);

    @Operation(summary = "블랙리스트 해제 API", description = "요양보호사를 블랙리스트에서 해제합니다.")
    @ApiResponse(responseCode = "204", description = "블랙리스트 해제 성공")
    @DeleteMapping("/blacklist")
    ResponseEntity<Void> deleteBlacklistByConsumer(@RequestParam UUID caregiverBlacklistId);

    @Operation(summary = "(마이페이지) 블랙리스트 조회 API", description = "특정 신고자가 신고한 블랙리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "블랙리스트 조회 성공")
    @GetMapping("/my-page/blacklist")
    ResponseEntity<List<GetBlacklistByConsumerResponse>> getBlacklistByConsumer(@RequestParam UUID consumerId);

    /**
     *
     * 리뷰 관련 API
     */

    @Operation(summary = "리뷰 등록 API", description = "서비스 매칭에 대한 새로운 리뷰를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 등록 성공")
    @PostMapping("/review")
    ResponseEntity<UUID> createReview(@RequestBody CreateReviewRequest request) throws AccessDeniedException;

    // TODO : 프론트엔드 연동 시, 일정 조회 -> 리뷰 조회 연동 과정에서 필요없다면 제거해도 됩니다.
    @Operation(summary = "매칭 일정의 리뷰 조회 API", description = "서비스 매치 ID를 통해 해당 매치의 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @GetMapping("/serviceMatch/{serviceMatchId}")
    ResponseEntity<GetReviewResponse> getReviewByServiceMatch(@RequestParam UUID serviceMatchId);

    @Operation(summary = "(마이페이지) 수요자 리뷰 조회 API", description = "수요자가 작성한 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 작성한 리뷰 조회 성공")
    @GetMapping("/my-page/review")
    ResponseEntity<List<ConsumerReviewResponse>> getWrittenReviews(@RequestParam UUID consumerId);


    @Operation(summary = "(마이페이지) 수요자가 작성해야 할 리뷰 조회 API", description = "수요자가 아직 리뷰를 작성하지 않은 매칭들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 아직 리뷰를 작성하지 않은 매칭 일정 조회 성공")
    @GetMapping("/my-page/review/pending")
    ResponseEntity<List<GetScheduleWithoutReviewResponse>> getPendingReviews(@RequestParam UUID consumerId);



}
