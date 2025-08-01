package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetServiceMatchByCenterResponse(
        String consumerName,
        String caregiverName,
        LocalDate serviceDate,
        LocalTime startTime,
        LocalTime endTime,
        String serviceType,
        MatchStatus status
) {}