package jaega.homecare.domain.consumer.dto.res;

import com.amazonaws.services.ec2.model.ServiceType;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

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
