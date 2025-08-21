package jaega.homecare.domain.Blacklist.dto.req;

import java.util.UUID;

public record CreateCaregiverBlacklistRequest(
    UUID caregiverId,
    UUID consumerId
) {
}