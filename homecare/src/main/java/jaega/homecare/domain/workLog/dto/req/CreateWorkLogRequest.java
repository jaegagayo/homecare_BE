package jaega.homecare.domain.workLog.dto.req;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateWorkLogRequest(
        UUID caregiverId,
        LocalTime workStartTime,
        LocalTime workEndTime,
        LocalDate workDate,
        String workAddress,
        Double distanceLog
) {
}
