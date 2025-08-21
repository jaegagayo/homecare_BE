package jaega.homecare.domain.matchReview.dto.req;

import java.util.UUID;

public record CreateReviewRequest(
        UUID consumerId,
        UUID serviceMatchId,
        Integer reviewScore,
        String reviewContent
) {
}