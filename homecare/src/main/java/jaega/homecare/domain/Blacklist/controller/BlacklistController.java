package jaega.homecare.domain.Blacklist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.Blacklist.dto.req.CreateCaregiverBlacklistRequest;
import jaega.homecare.domain.Blacklist.dto.res.GetCaregiverBlacklistResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "CaregiverBlacklist", description = "요양보호사 블랙리스트 API")
@RequestMapping("/api/blacklist")
public interface BlacklistController {

    @Operation(summary = "블랙리스트 생성 API", description = "요양보호사를 블랙리스트에 추가합니다.")
    @ApiResponse(responseCode = "201", description = "블랙리스트 생성 성공")
    @PostMapping("/caregiver")
    ResponseEntity<UUID> createCaregiverBlacklist(@RequestBody CreateCaregiverBlacklistRequest request);

    @Operation(summary = "블랙리스트 해제 API", description = "요양보호사를 블랙리스트에서 해제합니다.")
    @ApiResponse(responseCode = "204", description = "블랙리스트 해제 성공")
    @DeleteMapping("/caregiver")
    ResponseEntity<Void> deleteCaregiverBlacklist(@RequestParam UUID caregiverBlacklistId);

    @Operation(summary = "신고자별 블랙리스트 조회 API", description = "특정 신고자가 신고한 블랙리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "블랙리스트 조회 성공")
    @GetMapping("/consumer/{consumerId}")
    ResponseEntity<List<GetCaregiverBlacklistResponse>> getCaregiverBlacklistsByConsumer(@PathVariable UUID consumerId);
}