package jaega.homecare.domain.WorkLog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByPaid;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogResponse;
import jaega.homecare.domain.WorkLog.dto.res.GetWorkLogByDateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "WorkLog", description = "근무 기록 API")
@RequestMapping("/api/workLog")
public interface WorkLogController {

    @Operation(summary = "근무 기록 조회 API", description = "근무 기록의 ID를 기반으로 정보를 조회합니다.")
    @ApiResponse(responseCode = "204", description = "근무 기록 ID 기반 조회 성공")
    @GetMapping
    ResponseEntity<GetWorkLogResponse> getWorkLog(@RequestParam UUID workLogId);

    @Operation(summary = "특정 날짜의 근무 기록 조회 API", description = "특정 근무 날짜의 근무 기록들을 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 날짜의 근무 기록 조회 성공")
    @GetMapping("/workDay")
    ResponseEntity<List<GetWorkLogByDateResponse>> getWorkLogByWorkDay(@RequestParam UUID centerId, @RequestParam LocalDate workDate);

    @Operation(summary = "정산 상태 기반 근무 기록 조회 API", description = "정산 상태를 기반으로 특정 근무 기록들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 정산 상태 기반 근무 기록 조회 성공")
    @GetMapping("/paid")
    ResponseEntity<List<GetWorkLogByPaid>> getWorkLogByPaid(
            @RequestParam UUID centerId,
            @Parameter(
                    description = "정산 여부 (true: 정산 완료, false: 미정산)",
                    example = "true",
                    required = true
            )
            @RequestParam Boolean isPaid);
}
