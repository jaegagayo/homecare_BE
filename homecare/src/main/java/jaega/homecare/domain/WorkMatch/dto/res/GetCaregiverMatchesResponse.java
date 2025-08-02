package jaega.homecare.domain.WorkMatch.dto.res;

import jaega.homecare.domain.WorkMatch.entity.WorkStatus;

import java.time.LocalDate;
import java.util.UUID;

public record GetCaregiverMatchesResponse(
        UUID workMatchId,
        LocalDate workDate,
        WorkStatus status
) {
}
