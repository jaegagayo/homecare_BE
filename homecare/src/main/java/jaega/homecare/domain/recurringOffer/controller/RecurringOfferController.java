package jaega.homecare.domain.recurringOffer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "RecurringOffer", description = "RecurringOffer(정기 제안) 서비스 API")
@RequestMapping("/api/recurringOffer")
public interface RecurringOfferController {

    /**
     * caregiver
     */

    @Operation(summary = "(메인 페이지) 요양보호사의 정기 제안 알림용 조회 API", description = "메인 페이지에서 요양보호사의 신규 정기 제안을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "요양보호사의 정기 제안 알림 조회 성공")
    @GetMapping("/caregiver")
    ResponseEntity<List<GetCaregiverRecurringOfferSummaryResponse>> getRecurringOfferSummaryByCaregiver(@RequestParam UUID caregiverId);

    @Operation(summary = "요양보호사의 정기 제안 승인 API", description = "요양보호사의 해당 정기 제안을 승인하여 일정에 반영합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사가 해당 정기 제안을 승인하여 일정 반영 성공")
    @PostMapping("/caregiver/approve")
    ResponseEntity<Void> approveRecurringStatus(@RequestBody UUID recurringStatusId);

    @Operation(summary = "요양보호사의 정기 제안 거절 API", description = "요양보호사가 해당 정기 제안을 거절합니다.")
    @ApiResponse(responseCode = "204", description = "요양보호사가 해당 정기 제안을 거절 성공")
    @PostMapping("/caregiver/reject")
    ResponseEntity<Void> rejectRecurringStatus(@RequestParam UUID recurringStatusId);
}
