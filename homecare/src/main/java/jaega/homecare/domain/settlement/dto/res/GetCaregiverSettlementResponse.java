package jaega.homecare.domain.settlement.dto.res;

import java.math.BigDecimal;
import java.util.List;

public record GetCaregiverSettlementResponse(
        String centerName,
        Integer totalCount,      // 정산 건수
        Long totalHour,     // 총 시간
        Long totalDistanceLog,  // 총 이동 거리
        BigDecimal totalSettlementAmount,    // 총 금액
        List<GetCaregiverSettlementDetailDto> settlementDetailList
) {
}
