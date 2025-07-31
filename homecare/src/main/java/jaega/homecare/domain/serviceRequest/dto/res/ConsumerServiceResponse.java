package jaega.homecare.domain.serviceRequest.dto.res;

import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;

import java.time.LocalTime;
import java.util.UUID;

public record ConsumerServiceResponse(
        UUID serviceRequestId,
        String location,
        LocalTime preferred_time_start,
        LocalTime preferred_time_end,
        String serviceType,
        ServiceRequestStatus status,
        String personalityType,
        String requestedDays
) {
}
