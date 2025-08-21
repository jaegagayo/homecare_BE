package jaega.homecare.domain.workLog.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetWorkLogByDateResponse(
        UUID workLogId,
        LocalDate workingDate,
        LocalTime workTimeStart,
        LocalTime workTimeEnd,
        String caregiverName
) {}
