package jaega.homecare.domain.serviceMatch.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.settlement.dto.res.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "serviceMatch", description = "매칭, 스케줄 관련 조회")
@RequestMapping("/api/serviceMatch")
public interface ServiceMatchController {

    @Operation(summary = "특정 날짜의 근무 기록 조회 API", description = "특정 근무 날짜의 근무 기록들을 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 날짜의 근무 기록 조회 성공")
    @GetMapping("/workDay")
    ResponseEntity<List<GetSettlementByDateResponse>> getServiceMatchByWorkDay(@RequestParam UUID centerId, @RequestParam LocalDate workDate);
}
