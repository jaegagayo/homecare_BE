package jaega.homecare.domain.match.dto.req;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public record ServiceRequestDTO(
        UUID serviceRequestId,
        UUID consumerId,
        String serviceAddress,
        String addressType,
        Map<String, Object> location,
        String requestDate,
        String preferredStartTime,
        String preferredEndTime,
        int duration,
        String serviceType,
        String additionalInformation
) {}