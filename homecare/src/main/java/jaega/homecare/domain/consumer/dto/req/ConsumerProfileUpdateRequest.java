package jaega.homecare.domain.consumer.dto.req;

import jaega.homecare.domain.users.dto.req.UserUpdateRequest;

public record ConsumerProfileUpdateRequest(
        UserUpdateRequest userRequest,
        ConsumerUpdateRequest consumerRequest
) {
}
