package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetServiceMatchByConsumerResponse(
        String consumerName,
        String caregiverName,
        String caregiverAddress,
        String caregiverPhoneNumber,
        LocalDate serviceDate,
        LocalTime startTime,
        LocalTime endTime,
        ServiceType serviceType
) {
}
