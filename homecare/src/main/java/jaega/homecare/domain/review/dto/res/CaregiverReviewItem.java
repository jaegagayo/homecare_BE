package jaega.homecare.domain.review.dto.res;

import java.util.UUID;

public record CaregiverReviewItem(
        UUID reviewId,
        String scheduleSummary,
        Double reviewScore,
        String reviewContent
) {
}
