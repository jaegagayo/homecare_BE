package jaega.homecare.domain.review.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsumerPendingReviewResponse(
        UUID serviceMatchId,
        String caregiverName,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        ServiceType serviceType
) {
}
