package jaega.homecare.domain.caregiverPreference.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.req.UpdateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.dto.res.GetCaregiverPreferenceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "CaregiverPreference", description = "요양보호사의 선호(근무) 조건 API")
@RequestMapping("/api/caregiver/preference")
public interface CaregiverPreferenceController {

    @Operation(summary = "요양보호사 선호(근무) 조건 작성 API", description = "입력받은 정보로 요양보호사의 선호(근무) 조건을 생성합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사의 선호(근무) 조건 생성 성공")
    @PostMapping
    ResponseEntity<Void> createCaregiverPreference(
            @RequestParam UUID caregiverId,
            @RequestBody CreateCaregiverPreferenceRequest request);

    @Operation(summary = "요양보호사의 선호(근무) 조건 조회 API", description = "요양보호사가 자신의 선호(근무) 조건을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사의 선호(근무)조건 조회 성공")
    @GetMapping
    ResponseEntity<GetCaregiverPreferenceResponse> getCaregiverPreferenceByCaregiver(@RequestParam UUID caregiverId);

    @Operation(summary = "(마이페이지) 요양보호사 선호 조건 수정 API", description = "요양보호사가 자신의 선호(근무) 조건을 조회합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사 선호(근무)조건 수정 성공")
    @PutMapping
    ResponseEntity<Void> updateCaregiverPreference(@RequestParam UUID caregiverId,
                                                   @RequestBody UpdateCaregiverPreferenceRequest request);
}
