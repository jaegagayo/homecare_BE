package jaega.homecare.domain.serviceRequest.dto.res;

import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConsumerServiceResponse(
        UUID serviceRequestId,
        String location,
        LocalDateTime preferred_time_start,
        LocalDateTime preferred_time_end,
        String serviceType,
        ServiceRequestStatus status,
        String personalityType,
        String requestedDays
) {
}
