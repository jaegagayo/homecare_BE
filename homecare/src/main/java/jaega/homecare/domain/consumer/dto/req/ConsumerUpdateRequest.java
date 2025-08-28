package jaega.homecare.domain.consumer.dto.req;

import jaega.homecare.domain.consumer.entity.CognitiveStatus;
import jaega.homecare.domain.users.entity.Disease;

public record ConsumerUpdateRequest(
        String residentialAddress,
        String visitAddress,
        String entranceType,
        Integer careGrade,
        boolean medicalAid,
        Integer weight,
        Disease disease,
        CognitiveStatus cognitiveStatus,
        String livingSituation,
        String guardianName,
        String guardianPhone
) {
}
