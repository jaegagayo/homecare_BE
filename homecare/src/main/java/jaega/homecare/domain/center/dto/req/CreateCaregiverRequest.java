package jaega.homecare.domain.center.dto.req;

import java.time.LocalDate;

public record CreateCaregiverRequest(
        String name,
        String email,
        String phone,
        LocalDate birthDate,
        String address
){ }
