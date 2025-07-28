package jaega.homecare.domain.users.dto.res;

import java.util.UUID;

public record UserLoginResponse (
        UUID userId
) {
}
