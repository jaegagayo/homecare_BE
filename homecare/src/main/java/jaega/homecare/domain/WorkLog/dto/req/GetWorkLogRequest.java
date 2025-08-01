package jaega.homecare.domain.WorkLog.dto.req;

import java.util.UUID;

public record GetWorkLogRequest (
        UUID workMatchId
) {
}
