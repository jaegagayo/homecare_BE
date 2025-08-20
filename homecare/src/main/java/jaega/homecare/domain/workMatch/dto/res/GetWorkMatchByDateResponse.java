package jaega.homecare.domain.workMatch.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetWorkMatchByDateResponse(
        UUID workMatchId,
        LocalDate workingDate,
        LocalTime workTimeStart,
        LocalTime workTimeEnd,
        String caregiverName
) {}
