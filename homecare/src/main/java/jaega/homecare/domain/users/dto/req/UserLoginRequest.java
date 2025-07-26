package jaega.homecare.domain.users.dto.req;

public record UserLoginRequest(
        String email,
        String password
) {
}
