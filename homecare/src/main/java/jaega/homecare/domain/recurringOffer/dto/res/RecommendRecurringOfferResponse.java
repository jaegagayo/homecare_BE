package jaega.homecare.domain.recurringOffer.dto.res;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record RecommendRecurringOfferResponse(
        UUID serviceMatchId,
        UUID caregiverId,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Set<DayOfWeek> dayOfWeek
) {
}
