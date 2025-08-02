package jaega.homecare.domain.consumer.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ConfirmCaregiverRequest(
        UUID serviceRequestId,
        UUID caregiverId,

        String location,
        Double distanceLog
) {
}
