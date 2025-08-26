package jaega.homecare.domain.recurringOffer.dto.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.recurringOffer.entity.RecurringStatus;
import jaega.homecare.domain.serviceRequest.dto.req.LocationDto;
import jaega.homecare.domain.serviceRequest.entity.AddressType;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.global.util.DurationSerializer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record GetRecurringOfferDetailResponse(
        UUID recurringOfferId,
        String caregiverName,
        String consumer,
        String serviceAddress,
        AddressType addressType,
        Set<DayOfWeek> dayOfWeek,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,

        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime serviceStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime serviceEndTime,

        @Schema(example = "3h30m")
        @JsonSerialize(using = DurationSerializer.class)
        Integer duration,

        ServiceType serviceType,
        RecurringStatus recurringStatus
) {
}
