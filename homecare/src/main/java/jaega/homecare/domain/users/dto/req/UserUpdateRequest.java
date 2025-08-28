package jaega.homecare.domain.users.dto.req;

import jaega.homecare.domain.users.entity.Gender;

import java.time.LocalDate;

public record UserUpdateRequest(
        String name,
        LocalDate birthDate,
        Gender gender,
        String phone
) {
}
