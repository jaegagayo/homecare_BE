package jaega.homecare.domain.recurringOffer.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetUnreadRecurringOfferResponse(
        UUID recurringOfferId,
        String caregiverName,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,

        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime serviceStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime serviceEndTime,
        RecurringStatus recurringStatus
) {
}
