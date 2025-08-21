package jaega.homecare.domain.matchReview.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetReviewResponse(
        UUID reviewId,
        UUID serviceMatchId,
        Integer reviewScore,
        String reviewContent,
        LocalDateTime createdAt
) {
}