package jaega.homecare.domain.review.dto.res;

import java.util.UUID;

public record ConsumerPendingReviewResponse(
        UUID serviceMatchId,
        String serviceDate,
        String caregiverName
) {
}
