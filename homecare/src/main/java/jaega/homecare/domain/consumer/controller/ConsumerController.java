package jaega.homecare.domain.consumer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerSignupRequest;
import jaega.homecare.domain.consumer.dto.res.ConsumerNextScheduleResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleDetailResponse;
import jaega.homecare.domain.consumer.dto.res.ConsumerScheduleResponse;
import jaega.homecare.domain.consumer.dto.res.ReviewRequestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
