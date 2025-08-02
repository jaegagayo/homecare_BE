package jaega.homecare.domain.WorkLog.dto.res;

import java.time.LocalDate;
import java.util.UUID;

public record GetWorkLogByPaid(
        UUID workLogId,
        LocalDate workingDate,
        String caregiverName
) {
}
