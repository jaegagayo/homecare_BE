package jaega.homecare.domain.caregiver.dto.res;

import java.time.LocalDate;

public record GetCertificationResponse(
        String certificationNumber,
        LocalDate certificationDate,
        boolean trainStatus
) {
}
