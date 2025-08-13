package jaega.homecare.domain.WorkMatch.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.WorkMatch.dto.res.GetCaregiverWorkResponse;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "WorkMatch", description = "근무 일자 관련 조회 API")
@RequestMapping("/api/workMatch")
public interface WorkMatchController {

    @Operation(summary = "정산 페이지 요양 보호사 내역 조회 ", description = "정산 페이지의 요양 보호사 내역들을 조회합니다.")
    @ApiResponse(responseCode = "204", description = "수요자가 서비스 요청 성공")
    @GetMapping(("/{centerId}/caregivers/work"))
    ResponseEntity<List<GetCaregiverWorkResponse>> getCaregiverWorkList(
            @PathVariable UUID centerId,
            @RequestParam(required = false) WorkStatus status,
            @RequestParam int year,
            @RequestParam int month
    );
}
