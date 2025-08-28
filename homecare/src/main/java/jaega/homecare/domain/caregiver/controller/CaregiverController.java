package jaega.homecare.domain.caregiver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.caregiver.dto.req.CaregiverSignupRequest;
import jaega.homecare.domain.caregiver.dto.req.ChoiceCaregiverCenterRequest;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverSignupResponse;
import jaega.homecare.domain.caregiver.dto.res.GetCaregiverVerifiedStatusResponse;
import jaega.homecare.domain.caregiver.dto.res.SelectableCaregiverCenter;
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

    @Operation(summary = "요양보호사 승인 상태 조회 API", description = "회원가입 후, 기관 선택으로 이동하기 전 승인 검증을 위해 승인 상태를 조회합니다.<br>" +
            "RequestParam으로 한 이유는, 추후 JWT 인증 로직 구현 시 대체를 편하게 하기 위함입니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사의 승인 상태 조회 성공")
    @GetMapping("/verified")
    ResponseEntity<GetCaregiverVerifiedStatusResponse> getCaregiverVerifiedStatus(@RequestParam UUID caregiverId);

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
}
