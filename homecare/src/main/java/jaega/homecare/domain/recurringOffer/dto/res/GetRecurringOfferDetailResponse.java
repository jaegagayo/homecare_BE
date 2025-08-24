package jaega.homecare.domain.recurringOffer.dto.res;

import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetRecurringOfferDetailResponse(
        UUID recurringOfferId,
        String caregiverName,
        String consumer,
        Set<DayOfWeek> dayOfWeek,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Integer duration,
        ServiceType serviceType,
        RecurringStatus recurringStatus
) {
}
