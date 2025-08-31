package jaega.homecare.domain.match.dto.res;

import java.util.List;

public record MatchingResponseDTO(
        String serviceRequestId,
        List<MatchedCaregiverDTO> matchedCaregivers,
        Integer totalCandidates,
        Integer matchedCount
) {
}
