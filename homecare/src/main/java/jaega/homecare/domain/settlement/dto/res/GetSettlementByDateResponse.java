package jaega.homecare.domain.settlement.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetSettlementByDateResponse(
        UUID settlementId,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        String caregiverName
) {}
