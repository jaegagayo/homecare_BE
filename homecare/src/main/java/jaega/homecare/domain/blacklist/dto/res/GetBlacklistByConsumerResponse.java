package jaega.homecare.domain.blacklist.dto.res;

import java.util.UUID;

public record GetBlacklistByConsumerResponse(
    UUID blacklistId,
    UUID caregiverId,
    String caregiverName,
    UUID consumerId,
    String consumerName
) {
}