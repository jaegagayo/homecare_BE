package jaega.homecare.domain.serviceRequest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.serviceRequest.dto.req.ConsumerServiceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "ServiceRequest", description = "ServiceRequest API")
@RequestMapping("/api/consumer/request")
public interface ServiceRequestController {

    @Operation(summary = "수요자 서비스 요청 API", description = "입력받은 정보로 수요자가 서비스를 요청합니다.")
    @ApiResponse(responseCode = "204", description = "수요자가 서비스 요청 성공")
    @PostMapping
    ResponseEntity<Void> createServiceRequest(@RequestBody ConsumerServiceRequest request);
}
