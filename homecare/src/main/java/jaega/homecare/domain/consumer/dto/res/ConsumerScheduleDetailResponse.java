package jaega.homecare.domain.consumer.dto.res;

import com.amazonaws.services.ec2.model.ServiceType;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

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
