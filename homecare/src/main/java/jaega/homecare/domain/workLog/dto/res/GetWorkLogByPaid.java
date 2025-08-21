package jaega.homecare.domain.workLog.dto.res;

import java.time.LocalDate;
import java.util.UUID;

public record GetWorkLogByPaid(
        UUID workLogId,
        LocalDate workingDate,
        String caregiverName
) {
}