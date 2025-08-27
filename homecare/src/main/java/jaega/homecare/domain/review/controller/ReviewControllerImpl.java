package jaega.homecare.domain.review.controller;

import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.dto.res.ConsumerPendingReviewResponse;
import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
import jaega.homecare.domain.review.service.command.ReviewCommandService;
import jaega.homecare.domain.review.service.query.ReviewQueryService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;

    /**
     *
     * 리뷰 생성 API
     */
    // TODO : JWT 인증 로직 구현 시 직접 consumerId에서 가져오는 게 아닌 인증값에서 가져오도록 할 것 !
    // TODO : 추가로, Exception 또한 JWT에서 하기 !!
    @Override
    public ResponseEntity<UUID> createReview(@RequestBody CreateReviewRequest request) throws AccessDeniedException {
        UUID reviewId = reviewCommandService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
    }

    /**
     *
     * 리뷰 조회 API
     */
    @Override
    public ResponseEntity<GetReviewResponse> getReviewByServiceMatch(@PathVariable UUID serviceMatchId) {
        GetReviewResponse response = reviewQueryService.getReviewByServiceMatch(serviceMatchId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<ConsumerReviewResponse>> getWrittenReviews(@PathVariable UUID consumerId) {
        List<ConsumerReviewResponse> reviews = reviewQueryService.getWrittenReviews(consumerId);
        return ResponseEntity.ok(reviews);
    }

    @Override
    public ResponseEntity<List<ConsumerPendingReviewResponse>> getPendingReviews(@PathVariable UUID consumerId) {
        List<ConsumerPendingReviewResponse> pending = serviceMatchQueryService.getPendingReviews(consumerId);
        return ResponseEntity.ok(pending);
    }
}