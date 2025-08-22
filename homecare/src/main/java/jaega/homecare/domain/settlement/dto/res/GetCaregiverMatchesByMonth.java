package jaega.homecare.domain.settlement.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetCaregiverMatchesByMonth(
        UUID settlementId,
        UUID caregiverId,
        String caregiverName,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Set<ServiceType> serviceType,
        String address,
        MatchStatus matchStatus
) {
}
