package jaega.homecare.domain.recurringOffer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.RecommendRecurringOfferResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "RecurringOffer", description = "RecurringOffer(정기 제안) 서비스 API")
@RequestMapping("/api/recurringOffer")
public interface RecurringOfferController {

    @Operation(summary = "정기 제안 신청 작성 API", description = "입력받은 정보로 수요자와 요양보호사 간의 정기 제안 신청을 생성합니다.")
    @ApiResponse(responseCode = "204", description = "정기 제안 신청 작성 성공")
    @PostMapping
    ResponseEntity<Void> createRecurringOffer(@RequestBody CreateRecurringOfferRequest request);


    @Operation(summary = "수요자의 정기 제안 신청 조회 API", description = "수요자가 작성한 정기 제안 신청서을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "수요자의 정기 제안 신청 조회 성공")
    @GetMapping("/consumer/{consumerId}")
    ResponseEntity<List<GetRecurringOfferResponse>> getRecurringOfferByConsumer(@PathVariable UUID consumerId);

    @Operation(summary = "(메인 페이지) 수요자의 정기 제안 추천 알림 조회", description = "메인 페이지에서 수요자에게 다음과 같은 조건에 맞는 정기 제안 알림을 조회합니다.<br>" +
            "1. 일정 리뷰 평점이 4점 이상<br>" +
            "2. 아직 정기 제안을 신청하지 않았음<br>" +
            "3. 매칭이 종료된 일정")
    @ApiResponse(responseCode = "200", description = "수요자에게 조건에 맞는 정기 제안 추천 알림 조회 성공")
    @GetMapping("/consumer/recommend")
    ResponseEntity<List<RecommendRecurringOfferResponse>> getRecommendRecurringOfferForConsumer(@RequestParam UUID consumerId);
}
