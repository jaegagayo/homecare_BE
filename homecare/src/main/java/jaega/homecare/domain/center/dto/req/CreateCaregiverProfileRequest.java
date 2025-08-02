package jaega.homecare.domain.center.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.users.entity.Location;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record CreateCaregiverProfileRequest(
        UUID caregiverId,

        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime availableStartTIme,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime availableEndTime,
        String address,
        Location location,
        Set<ServiceType> serviceTypes,
        Set<DayOfWeek> daysOff
) {
}
