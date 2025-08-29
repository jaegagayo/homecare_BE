package jaega.homecare.domain.review.dto.res;

import java.util.UUID;

public record CaregiverReviewDetailResponse(
        UUID reviewId,
        String scheduleDetail,
        Double reviewScore,
        String reviewContent
) {
}
