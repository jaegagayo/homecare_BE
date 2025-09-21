package jaega.homecare.domain.settlement.dto.res;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record GetCaregiverSettlementDetailDto(
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Double distanceLog,
        BigDecimal settlementAmount,
        boolean isPaid
) {}
