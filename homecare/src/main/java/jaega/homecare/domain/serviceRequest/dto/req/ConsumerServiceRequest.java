package jaega.homecare.domain.serviceRequest.dto.req;


import java.time.LocalTime;
import java.util.UUID;

public record ConsumerServiceRequest(
        UUID userId,
        String address,
        LocationDto location,
        LocalTime preferred_time_start,
        LocalTime preferred_time_end,
        String serviceType,
        String personalityType,
        String requestedDays,
        String additionalInformation
) {
}
