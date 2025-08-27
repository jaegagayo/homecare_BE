package jaega.homecare.domain.consumer.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsumerNextScheduleResponse(
        String caregiverName,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,

        String serviceAddress,
        ServiceType serviceType
) {
}
