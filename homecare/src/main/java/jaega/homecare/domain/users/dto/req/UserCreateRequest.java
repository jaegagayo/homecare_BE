package jaega.homecare.domain.users.dto.req;

public record UserCreateRequest(
        String name,
        String email,
        String password,
        String phone
) {
}
