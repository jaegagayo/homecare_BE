package jaega.homecare.domain.recurringOffer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.recurringOffer.dto.req.CreateRecurringOfferRequest;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferDetailResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetRecommendRecurringOfferResponse;
import jaega.homecare.domain.recurringOffer.dto.res.GetUnreadRecurringOfferResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "RecurringOffer", description = "RecurringOffer(정기 제안) 서비스 API")
@RequestMapping("/api/recurringOffer")
public interface RecurringOfferController {

    @Operation(summary = "정기 제안 신청 작성 API", description = "입력받은 정보로 수요자와 요양보호사 간의 정기 제안 신청을 생성합니다.<br>" +
            "정기 신청의 1회 서비스 소요 시간을 시간/분 단위로 입력받으며 다음과 같이 duration 입력하시면 됩니다." +
            "ex) 3h30m : 3시간 30분, 3h : 3시간")
    @ApiResponse(responseCode = "204", description = "정기 제안 신청 작성 성공")
    @PostMapping
    ResponseEntity<Void> createRecurringOffer(@RequestBody CreateRecurringOfferRequest request);

    @Operation(summary = "정기 제안 상세 조회 API", description = "작성된 정기 제안 신청서를 상세 조회합니다.<br>" +
            "정기 신청의 1회 서비스 소요 시간이 다음과 같은 형식으로 조회됩니다." +
            "ex) 3h30m : 3시간 30분, 3h : 3시간")
    @ApiResponse(responseCode = "200", description = "정기 제안 신청 상세 조회 성공")
    @GetMapping("/{recurringOfferId}")
    ResponseEntity<GetRecurringOfferDetailResponse> getRecurringOfferDetail(@PathVariable UUID recurringOfferId);

    @Operation(summary = "수요자의 정기 제안 신청 조회 API", description = "수요자가 작성한 정기 제안 신청서을 조회합니다." +
            "정기 신청의 1회 서비스 소요 시간이 다음과 같은 형식으로 조회됩니다." +
            "ex) 3h30m : 3시간 30분, 3h : 3시간")
    @ApiResponse(responseCode = "200", description = "수요자의 정기 제안 신청 조회 성공")
    @GetMapping("/consumer/{consumerId}")
    ResponseEntity<List<GetRecurringOfferResponse>> getRecurringOfferByConsumer(@PathVariable UUID consumerId);

    @Operation(summary = "(메인 페이지) 수요자의 정기 제안 추천 알림 조회", description = "메인 페이지에서 수요자에게 다음과 같은 조건에 맞는 정기 제안 알림을 조회합니다.<br>" +
            "1. 일정 리뷰 평점이 4점 이상<br>" +
            "2. 아직 정기 제안을 신청하지 않았음<br>" +
            "3. 매칭이 종료된 일정<br>" +
            "RequestParam인 이유는 추후 JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함입니다.")
    @ApiResponse(responseCode = "200", description = "수요자에게 조건에 맞는 정기 제안 추천 알림 조회 성공")
    @GetMapping("/consumer/recommend")
    ResponseEntity<List<GetRecommendRecurringOfferResponse>> getRecommendRecurringOffersForConsumer(@RequestParam UUID consumerId);

    @Operation(summary = "(메인 페이지) 수요자의 '읽지 않은' 정기 제안 상태 기반 알림 조회", description = "메인 페이지에서 수요자가 '읽지 않은(아직 해당 정기 제안의 상세 정보를 조회하지 않은)<br>" +
            " 정기 제안 상태 기반 알림을 조회합니다.<br>" +
            "1. 승인 대기(PENDING)<br>" +
            "2. 승인 완료(APPROVED)<br>" +
            "3. 거절(REJECTED)<br>" +
            "RequestParam인 이유는 추후 JWT 등으로 인증 기능 구현 시 엔드포인트에 영향 받지 않고 ID를 전달하기 위함입니다.")
    @ApiResponse(responseCode = "200", description = "수요자에게 조건에 맞는 정기 제안 추천 알림 조회 성공")
    @GetMapping("/consumer/unread")
    ResponseEntity<List<GetUnreadRecurringOfferResponse>> getUnreadRecurringOffersForConsumer(@RequestParam UUID consumerId);
}
