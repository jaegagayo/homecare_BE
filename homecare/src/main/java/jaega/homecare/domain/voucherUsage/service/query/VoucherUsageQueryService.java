package jaega.homecare.domain.voucherUsage.service.query;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.voucher.entity.Voucher;
import jaega.homecare.domain.voucher.repository.VoucherQueryRepository;
import jaega.homecare.domain.voucher.repository.VoucherRepository;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageDetail;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageResponse;
import jaega.homecare.domain.voucherUsage.entity.VoucherUsage;
import jaega.homecare.domain.voucherUsage.mapper.VoucherUsageMapper;
import jaega.homecare.domain.voucherUsage.repository.VoucherUsageQueryRepository;
import jaega.homecare.domain.voucherUsage.repository.VoucherUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherUsageQueryService {
    private static final double MINIMUM_COPAY_RATE = 0.15;      // 법정 부담률 15%
    private static final long DEFAULT_SERVICE_AMOUNT = 55350L;  // (하드코딩) 재가요양 3시간 기준 금액으로 고정

    private final VoucherQueryRepository voucherQueryRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final VoucherUsageQueryRepository voucherUsageQueryRepository;
    private final VoucherUsageMapper voucherUsageMapper;

    // 바우처 사용 안내 조회
    public VoucherUsageGuideResponse getVoucherUsageGuide(UUID voucherId, Long totalVoucherAmount){
        Long usedAmount = voucherUsageQueryRepository.findTotalUsageAmountByVoucherId(voucherId, ServiceRequestStatus.ASSIGNED);
        if(usedAmount == null) usedAmount = 0L;
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

    // 바우처 상세 내역 조회
    public VoucherUsageResponse getVoucherUsageSummary(UUID consumerId, int year, int month){
        YearMonth targetMonth = YearMonth.of(year, month);
        Voucher voucher = voucherQueryRepository.findByConsumerIdAndMonth(consumerId, targetMonth);

        // 두 상태를 한 번에 조회
        List<VoucherUsage> allUsage = voucherUsageQueryRepository.findByVoucherAndStatusIn(
                voucher, List.of(MatchStatus.COMPLETED, MatchStatus.CONFIRMED)
        );

        // 금액 계산
        long usedAmount = allUsage.stream()
                .filter(u -> u.getServiceMatch().getMatchStatus() == MatchStatus.COMPLETED)
                .mapToLong(VoucherUsage::getAmount)
                .sum();

        long expectedAmount = allUsage.stream()
                .filter(u -> u.getServiceMatch().getMatchStatus() == MatchStatus.CONFIRMED)
                .mapToLong(VoucherUsage::getAmount)
                .sum();

        long remainingAmount = voucher.getTotalAmount() - (usedAmount + expectedAmount);

        long totalCopay = allUsage.stream().mapToLong(VoucherUsage::getCopay).sum();
        long confirmedCopay = allUsage.stream()
                .filter(u -> u.getServiceMatch().getMatchStatus() == MatchStatus.COMPLETED)
                .mapToLong(VoucherUsage::getCopay)
                .sum();

        // 날짜 기준 정렬 (최신순)
        allUsage.sort(Comparator.comparing(u -> u.getServiceMatch().getServiceRequest().getRequestDate(), Comparator.reverseOrder()));

        // 응답 DTO 변환
        List<VoucherUsageDetail> usageList = allUsage.stream()
                .map(u -> new VoucherUsageDetail(
                        u.getServiceMatch().getServiceRequest().getRequestDate(),
                        u.getAmount(),
                        u.getCopay(),
                        u.getServiceMatch().getServiceRequest().getServiceType().name(),
                        u.getServiceMatch().getMatchStatus()
                ))
                .toList();

        return new VoucherUsageResponse(
                voucher.getTotalAmount(),
                usedAmount,
                expectedAmount,
                remainingAmount,
                confirmedCopay,
                totalCopay,
                usageList
        );
    }

}
