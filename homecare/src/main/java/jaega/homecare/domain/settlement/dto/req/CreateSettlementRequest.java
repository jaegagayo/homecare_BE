package jaega.homecare.domain.settlement.dto.req;


import java.util.UUID;

public record CreateSettlementRequest(
        UUID caregiverCenterId,
        UUID serviceMatchId,
        Double distanceLog
) {
}
