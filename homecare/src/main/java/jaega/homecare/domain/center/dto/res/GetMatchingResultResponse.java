package jaega.homecare.domain.center.dto.res;

import java.time.LocalDate;

import java.time.LocalTime;

public record GetMatchingResultResponse(
        String consumerName,
        String caregiverName,
        LocalDate workingDate,
        LocalTime startTime,
        LocalTime endTime
) {
}