package jaega.homecare.domain.serviceRequest.dto.req;


import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ConsumerServiceRequest(
        UUID consumerId,

        String serviceAddress,
        AddressType addressType,
        LocationDto location,

        LocalDate requestDate,
        @Schema(description = "선호 시작 시간", example = "09:00:00")
        LocalTime preferredStartTime,
        @Schema(description = "선호 종료 시간", example = "18:00:00")
        LocalTime preferredEndTime,
        Integer duration,

        ServiceType serviceType,
        String additionalInformation
) {
}
