package jaega.homecare.domain.voucherUsage.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record VoucherUsageGuideResponse(
        @Schema(description = "남은 지원 금액")
        Long remainingAmount,

        @Schema(description = "예상 소진액")
        Long expectedUsageAmount,

        @Schema(description = "예상 본인 부담금")
        Long expectedCopay,

        @Schema(description = "본인 부담률 15% 초과 여부")
        boolean isHighCopayRate
) { }
