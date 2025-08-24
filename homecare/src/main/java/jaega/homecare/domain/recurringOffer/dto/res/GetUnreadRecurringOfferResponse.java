package jaega.homecare.domain.recurringOffer.dto.res;

import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetUnreadRecurringOfferResponse(
        UUID recurringOfferId,
        String caregiverName,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        RecurringStatus recurringStatus
) {
}
