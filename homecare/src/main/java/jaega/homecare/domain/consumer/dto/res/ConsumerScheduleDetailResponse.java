package jaega.homecare.domain.consumer.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsumerScheduleDetailResponse(
        String caregiverName,
        String caregiverPhone,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Integer duration,

        String serviceAddress,
        ServiceType serviceType,
        MatchStatus matchStatus,

        Boolean hasReview               // 리뷰 존재 여부
) {
}
