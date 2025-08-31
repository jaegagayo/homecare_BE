package jaega.homecare.domain.review.dto.req;

import java.util.UUID;

public record CreateReviewRequest(
        UUID consumerId,
        UUID serviceMatchId,
        Double reviewScore,
        String reviewContent
) {
}