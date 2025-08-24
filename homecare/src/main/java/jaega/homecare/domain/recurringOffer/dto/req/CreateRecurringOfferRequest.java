package jaega.homecare.domain.recurringOffer.dto.req;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jaega.homecare.domain.users.entity.ServiceType;
import jaega.homecare.global.util.DurationDeserializer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record CreateRecurringOfferRequest(
        UUID caregiverId,
        UUID consumerId,
        Set<DayOfWeek> dayOfWeek,
        LocalDate serviceStartDate,
        LocalDate serviceEndDate,

        @Schema(type = "string", format = "time", example = "09:00:00")
        LocalTime serviceStartTime,

        @Schema(type = "string", format = "time", example = "18:00:00")
        LocalTime serviceEndTime,

        @Schema(description = "시간/분을 초 단위로 직렬화합니다. 예: 3시간 30분 → 12600", example = "3h30m")
        @JsonDeserialize(using = DurationDeserializer.class)
        Integer duration,
        ServiceType serviceType
) {
}
