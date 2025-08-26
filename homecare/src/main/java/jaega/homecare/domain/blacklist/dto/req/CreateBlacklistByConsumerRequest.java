package jaega.homecare.domain.blacklist.dto.req;

import java.util.UUID;

public record CreateBlacklistByConsumerRequest(
    UUID caregiverId,
    UUID consumerId
) {
}