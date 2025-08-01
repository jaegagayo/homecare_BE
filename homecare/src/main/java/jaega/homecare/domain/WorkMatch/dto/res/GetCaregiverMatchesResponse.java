package jaega.homecare.domain.WorkMatch.dto.res;

import jaega.homecare.domain.WorkMatch.entity.WorkStatus;

import java.time.LocalDate;

public record GetCaregiverMatchesResponse(
        LocalDate workDate,
        WorkStatus status
) {
}
