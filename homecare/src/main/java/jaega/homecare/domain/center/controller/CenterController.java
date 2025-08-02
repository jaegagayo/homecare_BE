package jaega.homecare.domain.center.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.caregiver.dto.req.CreateCertificationRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCertificationResponse;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.center.dto.req.*;
import jaega.homecare.domain.center.dto.res.*;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import jaega.homecare.domain.users.entity.ServiceType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Center", description = "Center 서비스 API")
@RequestMapping("/api/center")
public interface CenterController {

    @Operation(summary = "보호사 등록 API", description = "입력받은 정보로 새로운 요양 보호사를 등록합니다." +
                                                        "요양 보호사 계정은 최초 등록 시 자동으로 생성됩니다.")
    @ApiResponse(responseCode = "204", description = "요양 보호사 등록 성공")
    @PostMapping("/{centerId}/caregiver")
    ResponseEntity<Void> createCaregiver(@RequestBody CreateCaregiverRequest createCaregiverRequest, @PathVariable UUID centerId);

    @Operation(summary = "센터 로그인 API", description = "입력받은 정보로 센터의 로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "센터 로그인 성공")
    @PostMapping("/login")
    ResponseEntity<CenterLoginResponse> loginCenter(@RequestBody CenterLoginRequest request);

    @Operation(summary = "보호사 상세 정보 등록 API", description = "입력받은 정보로 요양보호사의 프로필을 등록합니다.")
    @ApiResponse(responseCode = "204", description = "요양 보호사 상세 정보 등록 성공")
    @PostMapping("/caregiver")
    ResponseEntity<Void> createCaregiverProfile(@RequestBody CreateCaregiverProfileRequest request);

    @Operation(summary = "보호사 목록 조회 API", description = "센터에 소속된 요양 보호사를 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양 보호사 전체 조회 성공")
    @GetMapping("/{centerId}/caregiver")
    ResponseEntity<List<GetCaregiverResponse>> getAllCaregivers(@PathVariable UUID centerId);

    @Operation(summary = "배정 내역 전체 조회 API", description = "배정된 신청자-요양보호사 전체 목록을 최신순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양 보호사 전체 조회 성공")
    @GetMapping("/{centerId}/assign")
    public ResponseEntity<List<GetServiceMatchByCenterResponse>> getAllMatchingResult(@PathVariable UUID centerId);

    @Operation(summary = "특정 요양 보호사의 매칭 스케줄 조회", description = "특정 요양 보호사의 매칭 스케줄들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 요양 보호사의 매칭 스케줄 조회 성공")
    @GetMapping("/schedule/{caregiverId}")
    ResponseEntity<List<GetCaregiverMatchesResponse>> getWorkMatchByCaregiver(@PathVariable UUID caregiverId);

    @Operation(summary = "특정 년도, 월의 요양보호사 매칭 스케줄 조회", description = "특정 년도, 월에 해당하는 요양보호사의 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 년도, 월의 요양보호사 매칭 스케줄 조회")
    @GetMapping("/schedule/date")
    ResponseEntity<List<GetCaregiverMatchesByMonth>> getMatchesByMonth(
            @RequestParam UUID centerId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer day
    );

    @Operation(summary = "센터의 특정 서비스 매칭 정보 조회 API", description = "센터에서 서비스 매칭 UUID를 기반으로 서비스 매칭 정보를 상세 조회합니다.<br>" +
            "센터에서 1. 배정 내역 전체 조회를 하고 리턴된 UUID를 기반으로 이 API를 호출하는 느낌으로 구현했습니다.")
    @ApiResponse(responseCode = "200", description = "특정 서비스 정보 조회 API")
    @GetMapping("/schedule/detail/{serviceMatchId}")
    ResponseEntity<GetServiceMatchByUUID> getMatchesByUUID(@PathVariable UUID serviceMatchId);

    @Operation(summary = "센터 요양보호사의 근무 상태 기반 조회 API", description = "센터의 요양보호사들의 근무 상태(근무 중, 휴직, ...)를 기반으로 요양보호사 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 근무 상태 기반 목록 조회 성공")
    @GetMapping("/{centerId}/caregiverStatus")
    ResponseEntity<List<GetCaregiverByCaregiverStatusResponse>> getCaregiversByWorkStatus(@PathVariable UUID centerId, @RequestParam CaregiverStatus status);

    @Operation(summary = "센터 요양보호사의 서비스 유형 기반 조회 API", description = "센터 요양보호사의 서비스 유형 상태(재가, 방문, ...)를 기반으로 요양보호사 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 서비스 유형 기반 조회 성공")
    @GetMapping("/{centerId}/serviceType")
    ResponseEntity<List<GetCaregiverByServiceTypeResponse>> getCaregiverByServiceType(@PathVariable UUID centerId, @RequestParam Set<ServiceType> serviceTypes);

    @Operation(summary = "요양보호사의 자격증 생성 API", description = "센터 요양보호사의 자격증 정보를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 자격증 정보 생성 성공")
    @PutMapping("/certification")
    ResponseEntity<Void> createCertification(@RequestBody CreateCertificationRequest request);

    @Operation(summary = "요양보호사의 자격증 조회 API", description = "센터 요양보호사의 자격증을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "센터 요양보호사의 자격증 정보 조회 성공")
    @GetMapping("/{caregiverId}/certification")
    ResponseEntity<GetCertificationResponse> getCertificationByCaregiver(@PathVariable UUID caregiverId);

    @Operation(summary = "요양보호사 자격증 교육 상태 전환 API", description = "요양보호사 자격증의 교육 완료 상태를 전환합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사 자격증의 교육 상태 전환 성공")
    @PostMapping("/certification/change")
    ResponseEntity<Void> changeTrainStatus(@RequestBody UUID certificationId);

    @Operation(summary = "요양보호사 인사카드 조회", description = "요양보호사의 ID로 인사카드를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사 인사카드 조회 성공")
    @GetMapping("/profile")
    ResponseEntity<GetCaregiverProfileResponse>getCaregiverProfile(@RequestParam UUID caregiverId);
}
