package jaega.homecare.domain.matchReview.dto.req;

import java.util.UUID;

public record CreateMatchReviewRequest(
        UUID serviceMatchId,
        UUID writerId,
        String caregiverName,
        Integer serviceMatchTime,
        Integer reviewScore,
        String reviewContent
) {
}