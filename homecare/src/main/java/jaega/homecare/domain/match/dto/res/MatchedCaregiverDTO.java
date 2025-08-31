package jaega.homecare.domain.match.dto.res;

import java.util.List;

public record MatchedCaregiverDTO(
        String caregiverId,
        String name,
        Double distance,
        Integer estimatedTravelTime,
        Double matchingScore,
        String address,
        String addressType,
        List<String> location,
        String career,
        String selfIntroduction

) {

}
