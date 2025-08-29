package jaega.homecare.domain.serviceMatch.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsumerCancelledScheduleResponse(
        UUID serviceMatchId,
        LocalDate serviceDate,
        LocalTime startTime,
        LocalTime endTime,
        String caregiverName
) {}
