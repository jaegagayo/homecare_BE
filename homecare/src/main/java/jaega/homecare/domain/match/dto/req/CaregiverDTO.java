package jaega.homecare.domain.match.dto.req;

import java.util.List;

public record CaregiverDTO(
        String caregiverId,
        String serviceType,
        String closedDays,
        String availableStartTime,
        String availableEndTime,
        String baseAddress,
        List<Double> baseLocation,
        String personalityType,
        int careerYears
) {}
