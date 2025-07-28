package jaega.homecare.domain.users.dto.req;

public record ConsumerCreateRequest(
        String name,
        String email,
        String password,
        String phone
) {
}
