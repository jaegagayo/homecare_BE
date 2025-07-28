package jaega.homecare.domain.serviceRequest.dto.req;


import java.time.LocalDateTime;
import java.util.UUID;

public record ConsumerServiceRequest(
        UUID userId,
        String location,
        LocalDateTime preferred_time_start,
        LocalDateTime preferred_time_end,
        String serviceType,
        String personalityType,
        String requestedDays,
        String additionalInformation
) {
}
