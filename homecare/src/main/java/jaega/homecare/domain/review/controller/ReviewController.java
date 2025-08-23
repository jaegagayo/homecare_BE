package jaega.homecare.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Tag(name = "Review", description = "리뷰 서비스 API")
@RequestMapping("/api/review")
public interface ReviewController {

    @Operation(summary = "리뷰 생성 API", description = "서비스 매칭에 대한 새로운 리뷰를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 생성 성공")
    @PostMapping
    ResponseEntity<UUID> createReview(@RequestBody CreateReviewRequest request) throws AccessDeniedException;

    @Operation(summary = "리뷰 조회 API", description = "서비스 매치 ID를 통해 해당 매치의 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @GetMapping("/serviceMatch/{serviceMatchId}")
    ResponseEntity<GetReviewResponse> getReviewByServiceMatch(@PathVariable UUID serviceMatchId);
}