package jaega.homecare.domain.match.dto.res;

import java.util.UUID;

public record CaregiverInfo(
        UUID caregiverId,
        String name,
        double distanceKm,
        int estimatedTravelTime,
        int matchScore,
        String matchReason,
        String address,
        String addressType,
        String location,
        String career,
        String selfIntroduction,
        boolean isVerified,
        String serviceType
) {}