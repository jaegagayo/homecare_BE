package jaega.homecare.domain.caregiver.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.caregiver.entity.KoreanProficiency;
import jaega.homecare.domain.caregiver.entity.VerifiedStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record CaregiverCreateRequest(
        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime availableStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime availableEndTime,
        String address,
        Set<ServiceType> serviceTypes,
        Set<DayOfWeek> dayOfWeek,

        @Schema(type = "integer", example = "5", description = "경력(년 단위)")
        Integer career,

        KoreanProficiency koreanProficiency,
        boolean isAccompanyOuting,
        String selfIntroduction,
        VerifiedStatus verifiedStatus
) {
}
