package jaega.homecare.domain.WorkMatch.dto.res;

import jaega.homecare.domain.WorkMatch.entity.WorkStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record GetCaregiverWorkResponse (
        String caregiverName,
        LocalDate workDate,
        LocalTime workStart,
        LocalTime workEnd,
        BigDecimal settlementAmount,
        WorkStatus status
) {}