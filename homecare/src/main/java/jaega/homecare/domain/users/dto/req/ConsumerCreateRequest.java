package jaega.homecare.domain.users.dto.req;

import java.time.LocalDate;

public record ConsumerCreateRequest(
        String name,
        String email,
        String password,
        String phone,
        LocalDate birthDate
) {
}
