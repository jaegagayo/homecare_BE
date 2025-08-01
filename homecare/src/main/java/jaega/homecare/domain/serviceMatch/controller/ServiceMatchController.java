package jaega.homecare.domain.serviceMatch.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.serviceMatch.dto.res.ServiceMatchNotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "ServiceMatch", description = "수요자 매칭 API")
@RequestMapping("/api/consumerSchedule")
public interface ServiceMatchController {

    @Operation(summary = "특정 센터의 요양보호사-수요자간 매칭 결과 조회")
    @ApiResponse(responseCode = "200", description = "특정 센터의 요양보호사-수요자간 매칭 결과 조회 성공")
    @GetMapping("/{centerId}/notifications")
    ResponseEntity<List<ServiceMatchNotificationResponse>> getCenterNotifications(@PathVariable UUID centerId);
}
