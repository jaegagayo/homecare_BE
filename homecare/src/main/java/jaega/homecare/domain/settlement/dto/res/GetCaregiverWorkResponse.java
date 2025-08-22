package jaega.homecare.domain.settlement.dto.res;


import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record GetCaregiverWorkResponse (
        String caregiverName,
        LocalDate workDate,
        LocalTime workStart,
        LocalTime workEnd,
        BigDecimal settlementAmount,
        MatchStatus matchStatus
) {}