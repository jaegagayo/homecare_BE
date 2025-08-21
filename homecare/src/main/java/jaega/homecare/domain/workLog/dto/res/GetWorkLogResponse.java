package jaega.homecare.domain.workLog.dto.res;

import java.time.LocalTime;
import java.util.UUID;

public record GetWorkLogResponse(
        UUID workLogId,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Double distanceLog,
        Boolean isPaid
) {
}
