package jaega.homecare.domain.match.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.match.dto.res.MatchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Match", description = "매칭 알고리즘 API")
@RequestMapping("/api/match")
public interface MatchController {

    @Operation(summary = "매칭 알고리즘 호출 API", description = "서비스 요청 ID를 기반으로 수요자의 매칭 알고리즘을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "매칭 알고리즘 호출 성공")
    @PostMapping("/process")
    ResponseEntity<MatchResponse> matchingProcess(UUID serviceRequestId);
}
