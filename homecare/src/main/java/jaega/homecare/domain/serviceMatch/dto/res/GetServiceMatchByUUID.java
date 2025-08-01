package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

public record GetServiceMatchByUUID(
        String consumerName,
        String caregiverName,
        String serviceDate,
        String startTime,
        String endTime,
        MatchStatus status
) {
}
