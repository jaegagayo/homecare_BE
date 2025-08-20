package jaega.homecare.domain.workMatch.dto.res;

import java.time.LocalTime;
import java.util.UUID;

public record GetWorkMatchResponse(
        UUID workMatchId,
        LocalTime workTime_start,
        LocalTime workTime_end,
        Double distanceLog,
        Boolean isPaid
) {
}
