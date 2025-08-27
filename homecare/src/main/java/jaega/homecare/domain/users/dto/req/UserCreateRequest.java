package jaega.homecare.domain.users.dto.req;


import jaega.homecare.domain.users.entity.Gender;

import java.time.LocalDate;

public record UserCreateRequest(
        // User 회원가입
        String name,
        String email,
        String password,
        String phone,
        LocalDate birthDate,
        Gender gender
) {
}
