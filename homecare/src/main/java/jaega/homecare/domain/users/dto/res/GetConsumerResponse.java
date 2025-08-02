package jaega.homecare.domain.users.dto.res;

public record GetConsumerResponse(
        String name,
        String email,
        String phone,
        String birthDate
) {
}
