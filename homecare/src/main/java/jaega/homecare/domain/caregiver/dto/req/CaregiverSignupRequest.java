package jaega.homecare.domain.caregiver.dto.req;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;

public record CaregiverSignupRequest(
        UserCreateRequest user,
        CaregiverCreateRequest caregiver,
        CreateCertificationRequest certification
) {
}
