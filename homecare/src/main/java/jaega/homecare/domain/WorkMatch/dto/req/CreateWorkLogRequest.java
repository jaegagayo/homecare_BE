package jaega.homecare.domain.WorkMatch.dto.req;

import jaega.homecare.domain.WorkMatch.entity.WorkMatch;

import java.time.LocalTime;

public record CreateWorkLogRequest(
        WorkMatch workMatch,
        LocalTime workTime_start,
        LocalTime workTime_end,
        Double distanceLog
) {
}
