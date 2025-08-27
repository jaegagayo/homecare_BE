package jaega.homecare.domain.recurringOffer.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.serviceRequest.dto.req.LocationDto;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.users.entity.ServiceType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record CreateRecurringOfferRequest(
        UUID caregiverId,
        UUID consumerId,
        String serviceAddress,
        AddressType addressType,
        LocationDto location,
        Set<DayOfWeek> dayOfWeek,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,

        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime serviceStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime serviceEndTime,

        ServiceType serviceType
) {
}
