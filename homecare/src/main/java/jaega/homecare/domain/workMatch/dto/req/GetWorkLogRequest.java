package jaega.homecare.domain.workMatch.dto.req;

import java.util.UUID;

public record GetWorkLogRequest (
        UUID workMatchId
) {
}
