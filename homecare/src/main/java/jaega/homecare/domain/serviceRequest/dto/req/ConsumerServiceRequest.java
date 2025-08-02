package jaega.homecare.domain.serviceRequest.dto.req;


import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record ConsumerServiceRequest(
        UUID userId,
        String address,
        LocationDto location,

        @Schema(description = "선호 시작 시간", example = "09:00:00")
        LocalTime preferred_time_start,

        @Schema(description = "선호 종료 시간", example = "18:00:00")
        LocalTime preferred_time_end,
        ServiceType serviceType,
        String personalityType,
        Set<LocalDate> requestedDays,
        String additionalInformation
) {
}
