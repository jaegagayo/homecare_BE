package jaega.homecare.domain.match.dto.req;

import java.util.List;

public record MatchRequest(
        ServiceRequestDTO request,
        List<CaregiverDTO> candidates
) {
}
