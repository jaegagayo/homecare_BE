package jaega.homecare.domain.review.dto.res;

import java.time.LocalDate;

public record ConsumerReviewResponse(
        LocalDate serviceDate,
        String caregiverName,
        Double reviewScore,
        String reviewContent
) {
}
