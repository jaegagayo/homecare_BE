package jaega.homecare.domain.settlement.dto.req;

import java.util.UUID;

public record GetWorkLogRequest (
        UUID workMatchId
) {
}
