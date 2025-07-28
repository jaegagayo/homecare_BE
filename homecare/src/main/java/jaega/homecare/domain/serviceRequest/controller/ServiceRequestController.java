package jaega.homecare.domain.serviceRequest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import jaega.homecare.domain.serviceRequest.dto.res.ConsumerServiceResponse;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ServiceRequest", description = "Consumer의 서비스 신청 API")
@RequestMapping("/api/consumer/request")
public interface ServiceRequestController {

    @Operation(summary = "수요자 서비스 요청 API", description = "입력받은 정보로 수요자가 서비스를 요청합니다.")
    @ApiResponse(responseCode = "204", description = "수요자가 서비스 요청 성공")
    @PostMapping
    ResponseEntity<Void> createServiceRequest(@RequestBody ConsumerServiceRequest request);

    @Operation(summary  = "수요자가 신청한 서비스 내역 조회 API", description = "수요자가 신청한 서비스를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 조회 성공")
    @GetMapping
    public ResponseEntity<List<ConsumerServiceResponse>> getConsumerServiceRequest(@RequestParam UUID userId);

    @Operation(summary  = "수요자가 신청한 서비스 내역 조회 API (신청 서비스 상태 조건)", description = "수요자가 신청한 서비스를 신청한 서비스의 상태를 조건으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자가 신청한 서비스 내역 신청 상태를 조건으로 조회 성공")
    @GetMapping("/status")
    public ResponseEntity<List<ConsumerServiceResponse>> getConsumerServiceRequestByStatus(@RequestParam UUID userId, ServiceRequestStatus status);
}
