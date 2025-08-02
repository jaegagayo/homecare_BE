package jaega.homecare.domain.caregiverBlacklist.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetCaregiverBlacklistResponse(
    UUID blacklistId,
    UUID caregiverId,
    String caregiverName,
    UUID reporterId,
    String reporterName,
    String reason,
    LocalDateTime createdAt
) {
}