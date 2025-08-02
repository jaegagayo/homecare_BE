package jaega.homecare.domain.caregiverBlacklist.dto.req;

import java.util.UUID;

public record CreateCaregiverBlacklistRequest(
    UUID caregiverId,
    UUID reporterId,
    String reason
) {
}