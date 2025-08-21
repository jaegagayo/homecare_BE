package jaega.homecare.domain.workLog.dto.req;

import java.util.UUID;

public record GetWorkLogRequest (
        UUID workMatchId
) {
}
