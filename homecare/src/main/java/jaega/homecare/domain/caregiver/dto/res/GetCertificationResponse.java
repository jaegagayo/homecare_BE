package jaega.homecare.domain.caregiver.dto.res;

import java.time.LocalDate;
import java.util.UUID;

public record GetCertificationResponse(
        UUID certificationId,
        String certificationNumber,
        LocalDate certificationDate,
        boolean trainStatus
) {
}
