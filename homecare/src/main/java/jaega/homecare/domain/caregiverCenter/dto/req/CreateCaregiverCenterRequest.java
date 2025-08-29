package jaega.homecare.domain.caregiverCenter.dto.req;

import java.util.UUID;

public record CreateCaregiverCenterRequest(
        UUID caregiverId,
        UUID centerId
) {
}
