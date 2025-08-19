package jaega.homecare.domain.WorkMatch.dto.req;

import java.util.UUID;

public record GetWorkLogRequest (
        UUID workMatchId
) {
}
