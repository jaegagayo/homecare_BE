package jaega.homecare.domain.serviceRequest.dto.res;

import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetServiceRequestResponse(
        UUID serviceRequestId,
        String address,
        LocalTime preferred_time_start,
        LocalTime preferred_time_end,
        ServiceType serviceType,
        ServiceRequestStatus status,
        String personalityType,
        Set<LocalDate> requestedDays
) {
}
