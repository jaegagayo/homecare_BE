package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

public record CenterScheduleDetailResponse(
        String consumerName,
        String caregiverName,
        String serviceDate,
        String serviceStartTime,
        String serviceEndTime,
        MatchStatus matchStatus
) {
}
