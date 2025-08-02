package jaega.homecare.domain.matchReview.controller;

import jaega.homecare.domain.matchReview.dto.req.CreateMatchReviewRequest;
import jaega.homecare.domain.matchReview.dto.res.GetMatchReviewResponse;
import jaega.homecare.domain.matchReview.service.command.MatchReviewCommandService;
import jaega.homecare.domain.matchReview.service.query.MatchReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/match-review")
public class MatchReviewControllerImpl implements MatchReviewController {

    private final MatchReviewCommandService matchReviewCommandService;
    private final MatchReviewQueryService matchReviewQueryService;

    @Override
    public ResponseEntity<UUID> createMatchReview(@RequestBody CreateMatchReviewRequest request) {
        UUID matchReviewId = matchReviewCommandService.createMatchReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(matchReviewId);
    }

    @Override
    public ResponseEntity<GetMatchReviewResponse> getMatchReviewByServiceMatchId(@PathVariable UUID serviceMatchId) {
        GetMatchReviewResponse response = matchReviewQueryService.getMatchReviewByServiceMatchId(serviceMatchId);
        return ResponseEntity.ok(response);
    }
}