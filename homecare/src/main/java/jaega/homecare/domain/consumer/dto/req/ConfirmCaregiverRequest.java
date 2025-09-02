package jaega.homecare.domain.consumer.dto.req;

import java.util.UUID;

public record ConfirmCaregiverRequest(
        UUID serviceRequestId,
        UUID caregiverId
) {
}
