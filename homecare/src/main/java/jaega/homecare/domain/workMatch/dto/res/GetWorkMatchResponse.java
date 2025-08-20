package jaega.homecare.domain.workMatch.dto.res;

import java.time.LocalTime;
import java.util.UUID;

public record GetWorkMatchResponse(
        UUID workMatchId,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Double distanceLog,
        Boolean isPaid
) {
}
