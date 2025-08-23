package jaega.homecare.domain.consumer.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsumerScheduleResponse(
        UUID serviceRequestId,

        String caregiverName,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,

        String serviceAddress,
        ServiceType serviceType,
        MatchStatus matchStatus

){ }
