package jaega.homecare.domain.review.dto.res;

import java.util.List;

public record CaregiverReviewSummaryResponse(
        Double averageScore,
        List<CaregiverReviewItem> reviews
) {
}
