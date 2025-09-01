package jaega.homecare.domain.review.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record CaregiverReviewItem(
        UUID reviewId,
        String consumerName,
        String reviewContent,
        Double reviewScore,
        LocalDateTime createdAt
) {
}
