package jaega.homecare.domain.consumer.dto.req;

import jaega.homecare.domain.users.dto.req.UserCreateRequest;

public record ConsumerSignupRequest (
        UserCreateRequest user,
        ConsumerCreateRequest consumer
){
}
