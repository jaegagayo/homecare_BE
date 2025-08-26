package jaega.homecare.domain.voucherUsage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "VoucherUsage", description = "바우처 사용내역(VoucherUsage) API")
@RequestMapping("/api/voucher-usage")
public interface VoucherUsageController {

    @Operation(summary = "바우처 사용 안내 API", description = "재가 요양 서비스 신청 시 바우처 사용 가능 여부와 예상 본인 부담금을 안내합니다." +
                                                            "단, 본인 부담률 15%를 초과한 경우에만 안내하도록 합니다.")
    @ApiResponse(responseCode = "200", description = "바우처 사용 안내 조회 성공")
    @GetMapping("/guide/{consumerId}")
    ResponseEntity<VoucherUsageGuideResponse> getVoucherUsageGuide(@PathVariable UUID consumerId);
}
