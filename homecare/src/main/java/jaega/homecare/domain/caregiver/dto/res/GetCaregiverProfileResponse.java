package jaega.homecare.domain.caregiver.dto.res;

import jaega.homecare.domain.caregiver.entity.KoreanProficiency;
import jaega.homecare.domain.caregiver.entity.VerifiedStatus;

import java.util.UUID;

public record GetCaregiverProfileResponse(
        UUID caregiverId,
        String caregiverName,
        String phone,
        String birthDate,
        String address,
        Integer career,
        KoreanProficiency koreanProficiency,
        boolean isAccompanyOuting,
        String selfIntroduction,
        VerifiedStatus verifiedStatus
) {
}
