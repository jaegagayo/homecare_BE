package jaega.homecare.domain.caregiver.dto.req;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCertificationRequest(
        String certificationNumber,
        LocalDate certificationDate
) {
}
