package jaega.homecare.domain.match.dto.res;

import java.util.List;
import java.util.UUID;

public record MatchingResponse(
        UUID serviceRequestId,
        List<CaregiverInfo> matchedCaregivers
) {
}
