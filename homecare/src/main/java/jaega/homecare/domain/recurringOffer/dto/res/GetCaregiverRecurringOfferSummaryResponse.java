package jaega.homecare.domain.recurringOffer.dto.res;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetCaregiverRecurringOfferSummaryResponse(
        UUID recurringOfferId,
        String consumerName,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Integer totalOccurrences,   // 총 회차
        Set<DayOfWeek> dayOfWeek

) {
}
