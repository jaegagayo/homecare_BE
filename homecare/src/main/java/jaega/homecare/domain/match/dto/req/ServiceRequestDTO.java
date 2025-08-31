package jaega.homecare.domain.match.dto.req;

import java.util.List;

public record ServiceRequestDTO(
        String serviceRequestId,
        String consumerId,
        String serviceAddress,
        String addressType,
        List<Double> location,
        String requestDate,
        String preferredStartTime,
        String preferredEndTime,
        String duration,
        String serviceType,
        String additionalInformation
) {}