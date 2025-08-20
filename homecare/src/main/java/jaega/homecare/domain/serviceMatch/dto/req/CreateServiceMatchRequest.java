package jaega.homecare.domain.serviceMatch.dto.req;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreateServiceMatchRequest(
        UUID serviceRequestId,
        UUID caregiverId,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        LocalDate serviceDate
) {
}
