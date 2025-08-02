package jaega.homecare.domain.match.dto.req;

import java.util.List;

public record ServiceRequestDTO(
        String serviceRequestId,
        String address,
        List<Double> location,
        String serviceType,
        List<String> requestedDays
) {}