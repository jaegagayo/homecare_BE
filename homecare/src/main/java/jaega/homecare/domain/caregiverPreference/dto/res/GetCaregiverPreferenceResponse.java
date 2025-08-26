package jaega.homecare.domain.caregiverPreference.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.caregiverPreference.entity.PreferredGender;
import jaega.homecare.domain.users.entity.Disease;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetCaregiverPreferenceResponse(
        UUID caregiverPreferenceId,
        Set<DayOfWeek> dayOfWeek,
        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime workStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime workEndTime,

        @Schema(type = "integer", example = "3600")
        Integer workMinTime,

        @Schema(type = "integer", example = "10800")
        Integer workMaxTime,

        @Schema(type = "integer", example = "1800")
        Integer availableTime,

        String workArea,
        String transportation,

        @Schema(type = "integer", example = "3600")
        Integer lunchBreak,

        @Schema(type = "integer", example = "1800")
        Integer bufferTime,

        Set<Disease> supportedConditions,

        @Schema(type = "integer", example = "60")
        Integer preferredMinAge,

        @Schema(type = "integer", example = "90")
        Integer preferredMaxAge,

        PreferredGender preferredGender,
        Set<ServiceType> serviceTypes
) {
}
