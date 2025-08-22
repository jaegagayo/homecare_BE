package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetServiceMatchByCenterResponse(
        String consumerName,
        String caregiverName,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        String serviceType,
        MatchStatus status
) {}