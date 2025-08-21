package jaega.homecare.domain.Blacklist.dto.req;

import java.util.UUID;

public record CreateBlacklistByConsumerRequest(
    UUID caregiverId,
    UUID consumerId
) {
}