package jaega.homecare.domain.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.dto.res.ConsumerPendingReviewResponse;
import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
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

    @Operation(summary = "수요자 리뷰 조회 API", description = "수요자가 작성한 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 작성한 리뷰 조회 성공")
    @GetMapping("/{consumerId}/written")
    ResponseEntity<List<ConsumerReviewResponse>> getWrittenReviews(@PathVariable UUID consumerId);

    @Operation(summary = "수요자가 작성해야할 리뷰 조회 API", description = "수요자가 아직 리뷰를 작성하지 않은 매칭들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 아직 리뷰를 작성하지 않은 매칭 일정 조회 성공")
    @GetMapping("/{consumerId}/pending")
    ResponseEntity<List<ConsumerPendingReviewResponse>> getPendingReviews(@PathVariable UUID consumerId);
}