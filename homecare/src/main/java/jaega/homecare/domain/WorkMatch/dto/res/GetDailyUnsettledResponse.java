package jaega.homecare.domain.WorkMatch.dto.res;

import java.time.LocalDate;

public record GetDailyUnsettledResponse(
        LocalDate date,
        Long count
) {
}
