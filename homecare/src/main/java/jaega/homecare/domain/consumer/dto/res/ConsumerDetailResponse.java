package jaega.homecare.domain.consumer.dto.res;

import jaega.homecare.domain.consumer.entity.CognitiveStatus;
import jaega.homecare.domain.users.entity.Disease;

public record ConsumerDetailResponse(
        String name,
        String birthDate,
        String gender,
        String phone,
        String guardianName,
        String guardianPhone,
        String residentialAddress,
        String visitAddress,
        String entranceType,
        Integer careGrade,
        boolean medicalAid,
        Integer weight,
        Disease disease,
        CognitiveStatus cognitiveStatus,
        String livingSituation
) {
}
