package jaega.homecare.domain.consumer.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ConfirmCaregiverRequest(
        UUID serviceRequestId,
        UUID caregiverId,

        @Schema(description = "근무 시작 시간 (HH:mm)", example = "09:00")
        LocalTime workTime_start,

        @Schema(description = "근무 종료 시간 (HH:mm)", example = "18:00")
        LocalTime workTime_end,
        List<LocalDate> working_days,
        String location,
        Double distanceLog
) {
}
