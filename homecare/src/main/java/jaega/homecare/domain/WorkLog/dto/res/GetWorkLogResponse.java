package jaega.homecare.domain.WorkLog.dto.res;

import java.time.LocalTime;

public record GetWorkLogResponse(
        LocalTime workTime_start,
        LocalTime workTime_end,
        Double distanceLog,
        Boolean isPaid
) {
}
