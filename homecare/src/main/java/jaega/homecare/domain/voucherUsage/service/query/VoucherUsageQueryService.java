package jaega.homecare.domain.voucherUsage.service.query;

import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.mapper.VoucherUsageMapper;
import jaega.homecare.domain.voucherUsage.repository.VoucherUsageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherUsageQueryService {
    private static final double MINIMUM_COPAY_RATE = 0.15;      // 법정 부담률 15%
    private static final long DEFAULT_SERVICE_AMOUNT = 55350L;  // (하드코딩) 재가요양 3시간 기준 금액으로 고정

    private final VoucherUsageQueryRepository voucherUsageQueryRepository;
    private final VoucherUsageMapper voucherUsageMapper;

    public VoucherUsageGuideResponse getVoucherUsageGuide(UUID voucherId, Long totalVoucherAmount){
        Long usedAmount = voucherUsageQueryRepository.findTotalUsageAmountByVoucherId(voucherId);
        Long remainingAmount = totalVoucherAmount - usedAmount;

        Long expectedUsageAmount = (long) (DEFAULT_SERVICE_AMOUNT * (1 - MINIMUM_COPAY_RATE));   // 예상 바우처 사용액
        Long expectedCopay = calculateExpectedCopay(remainingAmount, expectedUsageAmount);

        return voucherUsageMapper.toVoucherUsageGuideResponse(remainingAmount, expectedUsageAmount, expectedCopay);
    }
    
    // 예상 본인 부담금 계산
    public Long calculateExpectedCopay(Long remainingAmount, Long expectedUsageAmount){
        Long baseCopay = (long) (DEFAULT_SERVICE_AMOUNT * MINIMUM_COPAY_RATE);          // 기본 본인 부담금
        Long exceededAmount = Math.max(0L, expectedUsageAmount - remainingAmount);      // 초과 금액 계산

        return baseCopay + exceededAmount;
    }

}
