package jaega.homecare.domain.Blacklist.dto.res;

import java.util.UUID;

public record GetBlacklistByConsumerResponse(
    UUID blacklistId,
    UUID caregiverId,
    String caregiverName,
    UUID consumerId,
    String consumerName
) {
}