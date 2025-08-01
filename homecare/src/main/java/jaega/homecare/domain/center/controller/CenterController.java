package jaega.homecare.domain.center.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.center.dto.req.CenterLoginRequest;
import jaega.homecare.domain.center.dto.req.CreateCaregiverProfileRequest;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.center.dto.res.CenterLoginResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByCenterResponse;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByUUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestParam int year,
            @RequestParam int month
    );

    @Operation(summary = "센터의 특정 서비스 매칭 정보 조회 API", description = "센터에서 서비스 매칭 UUID를 기반으로 서비스 매칭 정보를 상세 조회합니다.<br>" +
            "센터에서 1. 배정 내역 전체 조회를 하고 리턴된 UUID를 기반으로 이 API를 호출하는 느낌으로 구현했습니다.")
    @ApiResponse(responseCode = "200", description = "특정 서비스 정보 조회 API")
    @GetMapping("/schedule/detail/{serviceMatchId}")
    ResponseEntity<GetServiceMatchByUUID> getMatchesByUUID(@PathVariable UUID serviceMatchId);
}
