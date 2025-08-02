package jaega.homecare.domain.match.dto.res;

import java.util.List;

public record MatchingResponseDto(
        String caregiverId,
        String availableStartTime,
        String availableEndTime,
        String address,
        List<Double> location,
        Double matchScore,
        String matchReason
) {

}
