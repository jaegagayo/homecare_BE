package jaega.homecare.domain.recurringOffer.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetRecommendRecurringOfferResponse(
        UUID serviceMatchId,
        UUID caregiverId,
        String caregiverName,
        LocalDate serviceDate,

        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime serviceStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime serviceEndTime,
        Set<DayOfWeek> dayOfWeek
) {
}
