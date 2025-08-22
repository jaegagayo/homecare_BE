package jaega.homecare.domain.caregiver.dto.res;

import java.util.UUID;

public record SelectableCaregiverCenter(
        UUID caregiverCenterId,
        String centerName,
        String centerPhone
) {
}
