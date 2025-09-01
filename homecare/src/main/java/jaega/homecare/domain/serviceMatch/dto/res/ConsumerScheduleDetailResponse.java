package jaega.homecare.domain.serviceMatch.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.domain.voucherUsage.service.command.ServiceFeeTable;

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
        String entranceType,
        ServiceType serviceType,
        MatchStatus matchStatus,
        Long hourlyRate,
        String additionalInformation,

        Boolean hasReview               // 리뷰 존재 여부
) {
}
