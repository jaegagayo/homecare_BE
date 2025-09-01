package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsumerNextScheduleResponse(
        UUID serviceMatchId,
        String caregiverName,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,

        String serviceAddress,
        ServiceType serviceType
) {
}
