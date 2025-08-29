package jaega.homecare.domain.consumer.dto.res;

import java.util.UUID;

public record ConsumerLoginResponse(
        UUID consumerId
) {
}
