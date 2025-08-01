package jaega.homecare.domain.consumer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.consumer.dto.req.ConfirmCaregiverRequest;
import jaega.homecare.domain.consumer.dto.req.ConsumerCreateRequest;
import jaega.homecare.domain.serviceMatch.dto.res.GetServiceMatchByConsumerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Consumer", description = "유저(consumer) API")
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

    @Operation(summary = "특정 수요자의 매칭 스케줄 조회", description = "특정 수요자의 매칭된 결과를 토대로 스케줄을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 수요자의 매칭 스케줄 조회 성공")
    @GetMapping("/schedule/{consumerId}")
    ResponseEntity<List<GetServiceMatchByConsumerResponse>> getMatchesByConsumer(@PathVariable UUID consumerId);
}
