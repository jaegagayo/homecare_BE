package jaega.homecare.domain.match.dto.res;

import java.util.UUID;

public record CaregiverInfo(
        UUID caregiverId,
        String name,
        String gender,
        int age,
        int experience,
        Double rating,
        String koreanProficiency,
        SpecialCaseExperience specialCaseExperience,
        boolean outingAvailable,
        Double rejectionRate,
        String selfIntroduction
) {}