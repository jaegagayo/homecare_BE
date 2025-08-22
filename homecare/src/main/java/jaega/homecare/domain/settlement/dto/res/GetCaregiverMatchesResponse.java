package jaega.homecare.domain.settlement.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetCaregiverMatchesResponse(
        UUID serviceMatchId,
        Long caregiverId,
        String caregiverName,
        String consumerName,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,

        Set<ServiceType> workType,
        String address,
        int hourlyWage,
        MatchStatus status,
        String notes
) {
}
