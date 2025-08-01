package jaega.homecare.domain.WorkLog.dto.res;

import java.time.LocalTime;
import java.util.UUID;

public record GetWorkLogResponse(
        UUID workLogId,
        LocalTime workTime_start,
        LocalTime workTime_end,
        Double distanceLog,
        Boolean isPaid
) {
}
