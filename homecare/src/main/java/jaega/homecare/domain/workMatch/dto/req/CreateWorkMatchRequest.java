package jaega.homecare.domain.workMatch.dto.req;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateWorkMatchRequest(
        UUID caregiverId,
        LocalTime workStartTime,
        LocalTime workEndTime,
        LocalDate workDate,
        String workAddress,
        Double distanceLog
) {
}
