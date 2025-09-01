package jaega.homecare.domain.settlement.dto.res;

import java.util.List;

public record GetCaregiverCenterSettlementResponse(
        String centerName,
        List<GetSettlementByCaregiverResponse> settlements
) {
}
