package jaega.homecare.domain.caregiver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.caregiver.dto.req.CaregiverSignupRequest;
import jaega.homecare.domain.caregiver.dto.req.ChoiceCaregiverCenterRequest;
import jaega.homecare.domain.caregiver.dto.res.CaregiverLoginResponse;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverSignupResponse;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverVerifiedStatusResponse;
import jaega.homecare.domain.caregiver.dto.res.SelectableCaregiverCenter;
import jaega.homecare.domain.serviceMatch.dto.res.CaregiverScheduleDetailResponse;
import jaega.homecare.domain.serviceMatch.dto.res.CaregiverScheduleResponse;
import jaega.homecare.domain.review.dto.res.CaregiverReviewDetailResponse;
import jaega.homecare.domain.review.dto.res.CaregiverReviewSummaryResponse;
import jaega.homecare.domain.users.dto.req.UserLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "Caregiver", description = "Caregiver 서비스 API")
@RequestMapping("/api/caregiver")
public interface CaregiverController {

    @Operation(summary = "요양보호사 회원가입 API", description = "입력받은 정보로 요양보호사의 회원가입을 진행합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사의 회원가입 성공")
    @PostMapping("/register")
    ResponseEntity<GetCaregiverSignupResponse> signupCaregiver(@RequestBody CaregiverSignupRequest request);

    @Operation(summary = "요양보호사 로그인 API", description = "입력받은 정보로 요양보호사의 로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사 로그인 성공")
    @PostMapping("/login")
    ResponseEntity<CaregiverLoginResponse> loginCaregiver(@RequestBody UserLoginRequest request);

    @Operation(summary = "요양보호사 승인 상태 조회 API", description = "회원가입 후, 기관 선택으로 이동하기 전 승인 검증을 위해 승인 상태를 조회합니다.<br>" +
            "RequestParam으로 한 이유는, 추후 JWT 인증 로직 구현 시 대체를 편하게 하기 위함입니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사의 승인 상태 조회 성공")
    @GetMapping("/verified")
    ResponseEntity<GetCaregiverVerifiedStatusResponse> getCaregiverVerifiedStatus(@RequestParam UUID caregiverId);

    /**
     *
     * 센터 관련 API
     */

    @Operation(
            summary = "소속 중인 센터 목록 조회",
            description = "해당 요양보호사가 소속된 활성화된 센터 목록을 조회합니다. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "활성 센터 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "해당 요양보호사 또는 센터 없음")
    })
    @GetMapping("/center")
    ResponseEntity<List<SelectableCaregiverCenter>> getMyActiveCenters(@RequestParam UUID caregiverId);

    @Operation(summary = "요양보호사 명시적 센터 등록", description = "요양보호사가 매칭된 일정에 대해 명시적으로 소속할 센터를 선택합니다." +
                                                                "기존에 존재하는 활성화된 센터들 중에서 선택 가능합니다.")
    @ApiResponse(responseCode = "204", description = "선택한 센터 기반으로 Settlement 생성 완료")
    @PostMapping("/center")
    ResponseEntity<Void> chooseCaregiverCenter(@RequestBody ChoiceCaregiverCenterRequest request);

    /**
     *
     * 일정 조회 API
     */

    @Operation(summary = "특정 요양보호사의 주간 스케줄 조회", description = "특정 요양보호사의 주간 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 수요자의 주간 스케줄 조회 성공")
    @GetMapping("/schedule")
    ResponseEntity<List<CaregiverScheduleResponse>> getCaregiverSchedule(@RequestParam UUID caregiverId);

    @Operation(summary = "특정 스케줄 상세 조회", description = "요양보호사가 선택한 특정 스케줄의 상세 정보를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "스케줄 상세 조회 성공")
    @GetMapping("/schedule/{id}")
    ResponseEntity<CaregiverScheduleDetailResponse> getScheduleDetail(@PathVariable UUID id);

    @Operation(summary = "(메인 페이지) 요양보호사의 당일 스케줄 조회", description = "요양보호사의 당일 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사의 당일 스케줄 조회 성공")
    @GetMapping("/home/today-schedule")
    ResponseEntity<List<CaregiverScheduleResponse>> getTodaySchedule(@RequestParam UUID caregiverId);

    @Operation(summary = "(메인 페이지) 요양보호사의 내일 예정 스케줄 조회", description = "요양보호사의 내일 예정인 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사의 당일 스케줄 조회 성공")
    @GetMapping("/home/next-schedule")
    ResponseEntity<List<CaregiverScheduleResponse>> getTomorrowSchedule(@RequestParam UUID caregiverId);


    @Operation(summary = "요양보호사에게 신청자가 남긴 리뷰 리스트 조회", description = "요양보호사를 대상으로 신청자가 남긴 리뷰들을 조회합니다.<br>" +
            "평균 점수가 계산되고, 리뷰 리스트들이 조회됩니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사에게 등록된 리뷰 리스트 조회 성공")
    @GetMapping("/review")
    ResponseEntity<CaregiverReviewSummaryResponse> getReviews(@PathVariable UUID caregiverId);

    @Operation(summary = "요양보호사에게 등록된 리뷰 상세 조회", description = "요양보호사에게 등록된 특정 리뷰를 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사에게 등록된 리뷰 상세 조회 성공")
    @GetMapping("/review/{reviewId}")
    ResponseEntity<CaregiverReviewDetailResponse> getReviewDetail(@PathVariable UUID reviewId);
}
