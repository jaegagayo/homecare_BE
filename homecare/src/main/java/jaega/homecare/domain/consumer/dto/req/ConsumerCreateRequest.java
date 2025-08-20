package jaega.homecare.domain.consumer.dto.req;

import jaega.homecare.domain.users.entity.Disease;

public record ConsumerCreateRequest(
        // Consumer 회원가입
        String residentialAddress,
        String visitAddress,
        String entranceType,
        Integer careGrade,
        boolean isMedicalAid,
        Integer weight,
        Disease disease,
        String livingSituation,
        String guardianName,
        String guardianPhone
) {
}
