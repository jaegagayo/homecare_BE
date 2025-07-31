package jaega.homecare.domain.center.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.center.dto.req.CreateCaregiverRequest;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
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

    @Operation(summary = "보호사 목록 조회 API", description = "센터에 소속된 요양 보호사를 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양 보호사 전체 조회 성공")
    @GetMapping("/{centerId}/caregiver")
    ResponseEntity<List<GetCaregiverResponse>> getAllCaregivers(@PathVariable UUID centerId);
}
