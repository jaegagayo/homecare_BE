package jaega.homecare.domain.consumer.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReviewRequestResponse(
        String caregiverName,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        ServiceType serviceType

) {
}
