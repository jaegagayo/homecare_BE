package jaega.homecare.domain.WorkMatch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverMatchesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "workMatch", description = "근무 매칭 API")
@RequestMapping("/api/schedule")
public interface WorkMatchController {

    @Operation(summary = "특정 요양 보호사의 매칭 스케줄 조회", description = "특정 요양 보호사의 매칭 스케줄들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 요양 보호사의 매칭 스케줄 조회 성공")
    @GetMapping("/{caregiverId}")
    ResponseEntity<List<GetCaregiverMatchesResponse>> getWorkMatchByCaregiver(@PathVariable UUID caregiverId);

    @Operation(summary = "특정 년도, 월의 요양보호사 매칭 스케줄 조회", description = "특정 년도, 월에 해당하는 요양보호사의 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 년도, 월의 요양보호사 매칭 스케줄 조회")
    @GetMapping("/date")
    ResponseEntity<List<GetCaregiverMatchesByMonth>> getMatchesByMonth(
            @RequestParam int year,
            @RequestParam int month
    );
}
