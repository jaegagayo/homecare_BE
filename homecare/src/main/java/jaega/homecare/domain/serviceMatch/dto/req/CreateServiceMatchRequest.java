package jaega.homecare.domain.serviceMatch.dto.req;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CreateServiceMatchRequest(
        UUID serviceRequestId,
        UUID caregiverId,
        LocalTime serviceTime_start,
        LocalTime serviceTime_end,
        List<LocalDate> service_days
) {
}
