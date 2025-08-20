package jaega.homecare.domain.serviceRequest.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.users.entity.Location;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetServiceRequestById(
        UUID serviceRequestId,
        String serviceAddress,
        @Schema(description = "선호 시작 시간", example = "09:00:00")
        LocalTime preferredStartTime,
        @Schema(description = "선호 종료 시간", example = "18:00:00")
        LocalTime preferredEndTime,
        ServiceType serviceType,
        ServiceRequestStatus status,
        LocalDate requestDate,
        Location location
) {
}
