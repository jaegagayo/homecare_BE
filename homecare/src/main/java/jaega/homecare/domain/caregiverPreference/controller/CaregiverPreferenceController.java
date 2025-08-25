package jaega.homecare.domain.caregiverPreference.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "CaregiverPreference", description = "요양보호사의 선호(근무) 조건 API")
@RequestMapping("/api/caregiver/preference")
public interface CaregiverPreferenceController {

    @Operation(summary = "요양보호사 선호(근무) 조건 작성 API", description = "입력받은 정보로 요양보호사의 선호(근무) 조건을 생성합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사의 선호(근무) 조건 생성 성공")
    @PostMapping("/{caregiverId}")
    ResponseEntity<Void> createCaregiverPreference(
            @PathVariable UUID caregiverId,
            @RequestBody CreateCaregiverPreferenceRequest request);
}
