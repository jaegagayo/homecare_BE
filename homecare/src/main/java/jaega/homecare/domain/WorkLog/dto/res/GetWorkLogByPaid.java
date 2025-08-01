package jaega.homecare.domain.WorkLog.dto.res;

import java.time.LocalDate;

public record GetWorkLogByPaid(
        LocalDate workingDate,
        String caregiverName
) {
}
