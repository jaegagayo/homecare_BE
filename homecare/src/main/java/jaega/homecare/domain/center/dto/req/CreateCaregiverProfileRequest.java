package jaega.homecare.domain.center.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.caregiver.entity.ServiceType;

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
        Set<ServiceType> serviceTypes,
        Set<DayOfWeek> daysOff
) {
}
