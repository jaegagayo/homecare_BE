package jaega.homecare.domain.center.dto.req;

import java.util.UUID;

public record SearchCaregiverResponse(
        UUID caregiverId,
        String name,
        String phone,
        String certificationNumber
) {
}
