package jaega.homecare.domain.workMatch.dto.res;

import java.time.LocalDate;
import java.util.UUID;

public record GetWorkMatchByPaid(
        UUID workMatchId,
        LocalDate workingDate,
        String caregiverName
) {
}
