package jaega.homecare.domain.match.dto.req;

import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;

import java.util.List;

public record CaregiverDTO(
        String caregiverId,
        String userId,
        String name,
        String address,
        String addressType,
        List<Double> location,
        String career,
        String koreanProficiency,
        boolean isAccompanyOuting,
        String selfIntroduction,
        String verifiedStatus,
        CaregiverPreference preferences
) {}
