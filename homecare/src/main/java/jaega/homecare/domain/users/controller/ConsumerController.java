package jaega.homecare.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.users.dto.req.ConsumerCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User_Consumer", description = "유저(consumer) API")
@RequestMapping("/api/consumer")
public interface ConsumerController {

    @Operation(summary = "수요자 회원가입 API", description = "입력받은 정보로 수요자의 회원가입을 진행합니다.")
    @ApiResponse(responseCode = "204", description = "수요자 회원가입 성공")
    @PostMapping("/register")
    ResponseEntity<Void> createConsumer(@RequestBody ConsumerCreateRequest request);

    @Operation(summary = "요양보호사 확정", description = "수요자가 배정된 요양보호사를 최종 확정합니다.")
    @ApiResponse(responseCode = "204", description = "수요자에게 요양보호자 배정 성공")
    @PostMapping("/confirm")
    ResponseEntity<Void> confirmCaregiver(@RequestBody ConfirmCaregiverRequest request);

}
