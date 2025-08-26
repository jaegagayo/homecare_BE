package jaega.homecare.domain.caregiver.dto.res;

import jaega.homecare.domain.caregiver.entity.VerifiedStatus;

import java.util.UUID;

public record GetCaregiverVerifiedStatusResponse(
        UUID caregiverId,
        VerifiedStatus verifiedStatus
) {
}
