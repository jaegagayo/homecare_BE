package jaega.homecare.domain.workMatch.dto.req;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record CreateWorkMatchRequest(
        UUID caregiverId,
        LocalTime workTime_start,
        LocalTime workTime_end,
        Set<LocalDate> working_days,
        String address,
        Double distanceLog
) {
}
