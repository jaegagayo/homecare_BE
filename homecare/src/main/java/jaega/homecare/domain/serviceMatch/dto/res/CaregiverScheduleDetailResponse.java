package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.consumer.entity.CognitiveStatus;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.Disease;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record CaregiverScheduleDetailResponse(
        String consumerName,
        String consumerPhone,

        String guardianName,
        String guardianPhone,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Integer duration,

        Integer careGrade,
        Disease disease,
        Integer weight,
        CognitiveStatus cognitiveStatus,
        String livingSituation,

        String serviceAddress,
        String entranceType,

        String additionalInformation,

        ServiceType serviceType,
        MatchStatus matchStatus
) {
}
