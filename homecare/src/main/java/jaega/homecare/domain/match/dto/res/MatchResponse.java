package jaega.homecare.domain.match.dto.res;

import java.util.List;

public record MatchResponse(
        List<MatchingResponseDto> matchedCaregivers,
        int totalMatches
) {
}
