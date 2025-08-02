package jaega.homecare.domain.serviceRequest.dto.res;

import java.util.UUID;

public record GetCreateServiceResponse(
        UUID serviceRequestId
) {
}
