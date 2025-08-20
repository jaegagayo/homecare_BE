package jaega.homecare.domain.workMatch.dto.req;

import jaega.homecare.domain.workMatch.entity.WorkMatch;

import java.time.LocalTime;

public record CreateWorkLogRequest(
        WorkMatch workMatch,
        LocalTime workTime_start,
        LocalTime workTime_end,
        Double distanceLog
) {
}
