package jaega.homecare.domain.caregiver.dto.req;

public record CaregiverLoginRequest(
        String email,
        String password
) {
}
