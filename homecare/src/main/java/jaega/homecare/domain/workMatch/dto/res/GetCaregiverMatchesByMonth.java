package jaega.homecare.domain.workMatch.dto.res;

import jaega.homecare.domain.workMatch.entity.WorkStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetCaregiverMatchesByMonth(
        UUID workMatchId,
        UUID caregiverId,
        String caregiverName,
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime,
        Set<ServiceType> serviceType,
        String address,
        WorkStatus status
) {
}
