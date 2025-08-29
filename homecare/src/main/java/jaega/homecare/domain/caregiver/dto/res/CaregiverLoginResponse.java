package jaega.homecare.domain.caregiver.dto.res;

import java.util.UUID;

public record CaregiverLoginResponse(
        UUID caregiverId
) {
}
