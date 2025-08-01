package jaega.homecare.domain.WorkLog.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;

public record GetWorkLogByDateResponse(
        LocalDate workingDate,
        LocalTime workTimeStart,
        LocalTime workTimeEnd,
        String caregiverName
) {}
