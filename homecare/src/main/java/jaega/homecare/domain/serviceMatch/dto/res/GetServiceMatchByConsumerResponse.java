package jaega.homecare.domain.serviceMatch.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetServiceMatchByConsumerResponse(
        String consumerName,
        String caregiverName,
        LocalDate serviceDate,
        LocalTime startTime,
        LocalTime endTime,
        String serviceType
) {
}
